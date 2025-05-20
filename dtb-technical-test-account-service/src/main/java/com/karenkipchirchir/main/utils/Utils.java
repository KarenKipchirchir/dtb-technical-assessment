package com.karenkipchirchir.main.utils;

import com.karenkipchirchir.main.repo.AccountDataEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class Utils {

    @Value("${properties.bankCode}")
    private String bankCode;

    @Value("${properties.countryCode}")
    private String countryCode;

    @Value("${properties.checkDigitsPlaceHolder}")
    private String checkDigitsPlaceHolder;

    @Value("${properties.accountNumbersStartingPoint}")
    private Integer accountNumbersStartingPoint;

    @Autowired
    private AccountDataEntityRepository accountDataEntityRepository;



    public String generateRequestHash(String messageId, Integer customerId) {


            try {
                // Combine request details into a single string
                String input = messageId + "|" + customerId + "|";

                // Create a SHA-256 MessageDigest instance
                MessageDigest digest = MessageDigest.getInstance("SHA-256");

                // Compute the hash
                byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

                // Encode the hash to Base64 for storage or transmission
                return Base64.getEncoder().encodeToString(hashBytes);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Error generating hash: SHA-256 algorithm not found.", e);
            }

    }


    public Integer generateAccountNumber() {

        Integer maxAccount = accountDataEntityRepository.findMaxAccountNumber();

        Integer nextNumber = accountNumbersStartingPoint;
        if (maxAccount != null) {
            try {
                nextNumber = maxAccount + 1;
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid account number in DB: " + maxAccount);
            }
        }

        return nextNumber;

    }




    public String generateIBAN(Integer accountNumber) {

        String accountNumberStr = String.format("%010d", accountNumber);

        String bankDetails = bankCode + accountNumberStr;

        String rearranged = bankDetails + countryCode + checkDigitsPlaceHolder;

        StringBuilder numericString = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isLetter(c))
                numericString.append(Character.getNumericValue(c));
            else
                numericString.append(c);
        }

        int checkDigit = 98 - new BigInteger(numericString.toString()).mod(BigInteger.valueOf(97)).intValue();

        return countryCode + String.format("%02d", checkDigit) + bankDetails;
    }





}
