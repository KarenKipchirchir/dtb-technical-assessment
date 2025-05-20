package com.karenkipchirchir.main.utils;

import com.karenkipchirchir.main.models.ProcessEnums;
import com.karenkipchirchir.main.models.database.AccountDataEntity;
import com.karenkipchirchir.main.models.database.BranchCodesEntity;
import com.karenkipchirchir.main.models.database.CustomerBioDataEntity;
import com.karenkipchirchir.main.models.request.AccountDataRequest;
import com.karenkipchirchir.main.repo.AccountDataEntityRepository;
import com.karenkipchirchir.main.repo.BranchCodesEntityRepository;
import com.karenkipchirchir.main.repo.CustomerDataRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RequestValidation {


    @Autowired
    CustomerDataRepo customerDataRepo;

    @Autowired
    Utils utils;

    @Value("${properties.idempotencyCheck}")
    public Integer lastCheckTime;


    @Value("${properties.maxAccounts}")
    public Integer maxAccounts;
    @Autowired
    private BranchCodesEntityRepository branchCodesEntityRepository;
    @Autowired
    private AccountDataEntityRepository accountDataEntityRepository;

    public AccountDataRequest validateAccountRequest(AccountDataRequest accountDataRequest, String requestType) {


        List<String> errorMessages = new ArrayList<>();
        String requestHash = null;

        if (requestType.equalsIgnoreCase("CREATE")) {

            if (accountDataRequest == null) {
                errorMessages.add("Request cannot be null");
                accountDataRequest = new AccountDataRequest();
            } else {


                if (checkIfStringIsValid(accountDataRequest.getMessageId())) {
                    errorMessages.add("Message ID cannot be null/empty");
                }

                try {
                    String customerId = String.valueOf(accountDataRequest.getCustomerId());
                    if (checkIfStringIsValid(customerId)) {
                        errorMessages.add("Customer ID cannot be null/empty");
                    } else {

                        try {

                            Optional<CustomerBioDataEntity> accountDataRequestEntity = customerDataRepo.findByCustomerId(accountDataRequest.getCustomerId());

                            if (accountDataRequestEntity.isEmpty()) {
                                errorMessages.add("Customer ID does not exist in system");
                            }else{
                                accountDataRequest.setCustomerBioData(accountDataRequestEntity.get());
                            }

                        } catch (Exception e) {
                            errorMessages.add("Customer ID validation failed");
                        }

                    }

                } catch (Exception ex) {
                    errorMessages.add("Customer ID must be a numeric value");
                }


                if (checkIfStringIsValid(accountDataRequest.getBicSwift())) {
                    errorMessages.add("BicSwift cannot be null/empty");
                }

                if (checkIfStringIsValid(accountDataRequest.getCurrency())) {
                    errorMessages.add("Currency cannot be null/empty");
                }


                try {
                    String branchCode = String.valueOf(accountDataRequest.getBranchCode());
                    if (checkIfStringIsValid(branchCode)) {
                        errorMessages.add("Branch Code cannot be null/empty");
                    } else {

                        try {

                            Optional<BranchCodesEntity> branchCodes = branchCodesEntityRepository.findByBranchCode(accountDataRequest.getBranchCode());

                            if (branchCodes.isEmpty()) {
                                errorMessages.add("Branch Code does not exist in system");
                            }else{
                                accountDataRequest.setBranchCodesEntity(branchCodes.get());
                            }

                        } catch (Exception e) {
                            errorMessages.add("Branch Code validation failed");
                        }

                    }

                } catch (Exception ex) {
                    errorMessages.add("Branch Code must be a numeric value");
                }

                
            }


        }else if (requestType.equalsIgnoreCase("UPDATE")) {
            if (checkIfStringIsValid(accountDataRequest.getMessageId())) {
                errorMessages.add("Message ID cannot be null/empty");
            }


            if (checkIfStringIsValid(accountDataRequest.getStatus())) {
                errorMessages.add("Status cannot be null/empty");
            } else if (!accountDataRequest.getStatus().equalsIgnoreCase(ProcessEnums.ACTIVE.name()) && !accountDataRequest.getStatus().equalsIgnoreCase(ProcessEnums.INACTIVE.name())) {
                errorMessages.add("Invalid status passed");
            }

            try {
                String accountId = String.valueOf(accountDataRequest.getAccountId());
                if (checkIfStringIsValid(accountId)) {
                    errorMessages.add("Account ID cannot be null/empty");
                }

            } catch (Exception ex) {
                errorMessages.add("Account ID must be a numeric value");
            }
        }else if (requestType.equalsIgnoreCase("DELETE")) {
            if (checkIfStringIsValid(accountDataRequest.getMessageId())) {
                errorMessages.add("Message ID cannot be null/empty");
            }

            try {
                String customerId = String.valueOf(accountDataRequest.getAccountId());
                if (checkIfStringIsValid(customerId)) {
                    errorMessages.add("Account ID cannot be null/empty");
                }

            } catch (Exception ex) {
                errorMessages.add("Account ID must be a numeric value");
            }
        }


        if (errorMessages.isEmpty()){
            LocalDateTime createdAfter  = LocalDateTime.now().minusMinutes(lastCheckTime);

           requestHash = utils.generateRequestHash(accountDataRequest.getMessageId(), accountDataRequest.getAccountId());

           try {
               Optional<AccountDataEntity> accountDataRequestEntity = accountDataEntityRepository.findByRequestHashAndCreatedAtAfter(requestHash,createdAfter);

               if (accountDataRequestEntity.isPresent()) {
                   errorMessages.add("Idempotency conflict detected");
               }
           } catch (Exception e) {
               errorMessages.add("Idempotency validation failed");
           }
        }


        if (errorMessages.isEmpty()){
            accountDataRequest.setIsValid(true);
            accountDataRequest.setRequestHash(requestHash);
        }else{
            accountDataRequest.setErrorMessages(errorMessages);
            accountDataRequest.setIsValid(false);
        }

        return accountDataRequest;
    }


    private Boolean checkIfStringIsValid(String string) {
        return string == null || string.trim().isEmpty();
    }
}
