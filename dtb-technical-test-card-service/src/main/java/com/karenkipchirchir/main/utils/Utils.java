package com.karenkipchirchir.main.utils;


import com.karenkipchirchir.main.repo.CardsEntityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@Service
public class Utils {

    @Value("${properties.cardNumbersStartingPoint}")
    private Integer cardNumbersStartingPoint;

    @Value("${properties.cvvExpiryMinutes}")
    private Integer cvvExpiryMinutes;


    private final CardsEntityRepository cardsEntityRepository;

    public Utils(CardsEntityRepository cardsEntityRepository) {
        this.cardsEntityRepository = cardsEntityRepository;
    }


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


    public Integer generateCardNumber() {

        Integer maxCard = cardsEntityRepository.findMaxCardNumber();

        Integer nextNumber = cardNumbersStartingPoint;
        if (maxCard != null) {
            try {
                nextNumber = maxCard + 1;
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid card number in DB: " + maxCard);
            }
        }

        return nextNumber;

    }



    public String maskPan(String pan) {

        String firstSix = pan.substring(0, 6);
        String lastFour = pan.substring(pan.length() - 4);
        String maskedSection = "*".repeat(pan.length() - 10);

        return firstSix + maskedSection + lastFour;
    }

    public String maskCvv() {
        return "***";
    }


    public String generatePrimaryAccountNumber(Integer accountId) {

        String accountNumber = String.valueOf(accountId);

        StringBuilder pan = new StringBuilder(accountNumber);

        Random random = new Random();
        for (int i = 0; i < 7; i++) {
            pan.append(random.nextInt(10));
        }

        return pan.toString();




    }

    public String generateCVV() {

        Random random = new Random();
        int cvv = 100 + random.nextInt(900);
        return String.valueOf(cvv);
    }

    public String generateExpiryDate() {
        LocalDate now = LocalDate.now();
        LocalDate expiryDate = now.plusYears(5);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        return expiryDate.format(formatter);
    }

    public Date generateCVVExpiryTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, cvvExpiryMinutes);
        return calendar.getTime();
    }
}
