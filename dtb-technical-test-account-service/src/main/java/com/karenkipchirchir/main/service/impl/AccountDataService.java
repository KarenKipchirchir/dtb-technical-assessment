package com.karenkipchirchir.main.service.impl;

import com.karenkipchirchir.main.models.ProcessEnums;
import com.karenkipchirchir.main.models.database.AccountDataEntity;
import com.karenkipchirchir.main.models.request.AccountDataRequest;
import com.karenkipchirchir.main.models.response.AccountDataResponse;
import com.karenkipchirchir.main.models.response.AccountDetails;
import com.karenkipchirchir.main.models.response.AccountDetailsResponseWrapper;
import com.karenkipchirchir.main.repo.AccountDataEntityRepository;
import com.karenkipchirchir.main.service.AccountDataInterface;
import com.karenkipchirchir.main.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AccountDataService implements AccountDataInterface {

    @Autowired
    private AccountDataEntityRepository accountDataEntityRepository;

    @Autowired
    Utils utils;

    @Override
    public ResponseEntity<?> processCreateAccountRequest(AccountDataRequest accountDataRequest) {


        Integer accountNumber = utils.generateAccountNumber();

        String iban = utils.generateIBAN(accountNumber);

        AccountDataEntity accountDataEntity = AccountDataEntity
                .builder()
                .customer(accountDataRequest.getCustomerBioData())
                .accountId(accountNumber)
                .iban(iban)
                .bicSwift(accountDataRequest.getBicSwift())
                .currency(accountDataRequest.getCurrency())
                .branch(accountDataRequest.getBranchCodesEntity())
                .status(ProcessEnums.ACTIVE.name())
                .requestHash(accountDataRequest.getRequestHash())
                .createdAt(LocalDateTime.now())
                .build();



        try{
            accountDataEntityRepository.save(accountDataEntity);

            String customerNames = accountDataRequest.getCustomerBioData().getFirstName()+ " " +accountDataRequest.getCustomerBioData().getLastName() + " "+ accountDataRequest.getCustomerBioData().getOtherName();


            AccountDataResponse accountDataRequestResponse = AccountDataResponse
                    .builder()
                    .messageId(accountDataRequest.getMessageId())
                    .statusCode("0")
                    .statusDescription("Success")
                    .errorInfoList(null)
                    .accountId(accountNumber)
                    .iban(iban)
                    .bicSwift(accountDataRequest.getBicSwift())
                    .accountCurrency(accountDataRequest.getCurrency())
                    .accountBranchCode(accountDataRequest.getBranchCode())
                    .accountStatus(ProcessEnums.ACTIVE.name())
                    .customerNames(customerNames)
                    .customerId(accountDataRequest.getCustomerId())
                    .build();


            return new ResponseEntity<>(accountDataRequestResponse, HttpStatus.OK);
        }catch (Exception ex){
            log.error("EXCEPTION OCCURRED WHILE SAVING ACCOUNT DATA ::: {}",ex.getLocalizedMessage());

            AccountDataResponse accountDataRequestResponse = AccountDataResponse
                    .builder()
                    .messageId(accountDataRequest.getMessageId())
                    .statusCode("1")
                    .statusDescription("Failure")
                    .errorInfoList(Collections.singletonList("Internal Server Error"))
                    .customerId(accountDataRequest.getCustomerId())
                    .bicSwift(accountDataRequest.getBicSwift())
                    .accountCurrency(accountDataRequest.getCurrency())
                    .accountBranchCode(accountDataRequest.getBranchCode())
                    .build();


            return new ResponseEntity<>(accountDataRequestResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public ResponseEntity<?> fetchAccountDetails(String messageId, String iban, String bicSwift, String cardAlias, Integer pageNumber, Integer pageSize) {
        List<AccountDetails> accountDetailsList = new ArrayList<>();

        Pageable paging = PageRequest.of(pageNumber, pageSize);


        List<AccountDataEntity> accountDataEntityList = accountDataEntityRepository.findByIbanAndBicSwiftAndCardsEntities_CardAliasLikeOrCardsEntities_CardAliasContains(
                iban,
                bicSwift,
                cardAlias,
                cardAlias,
                paging
        );

        for(AccountDataEntity accountDataEntity : accountDataEntityList) {

            String customerNames = accountDataEntity.getCustomer().getFirstName()+ " " +accountDataEntity.getCustomer().getLastName() + " "+ accountDataEntity.getCustomer().getOtherName();

            AccountDetails response = AccountDetails
                    .builder()
                    .accountId(accountDataEntity.getAccountId())
                    .iban(accountDataEntity.getIban())
                    .bicSwift(accountDataEntity.getBicSwift())
                    .accountStatus(accountDataEntity.getStatus())
                    .accountCurrency(accountDataEntity.getCurrency())
                    .accountBranchCode(accountDataEntity.getBranch().getBranchCode())
                    .customerId(accountDataEntity.getCustomer().getCustomerId())
                    .customerNames(customerNames)
                    .build();

            accountDetailsList.add(response);

        }


        AccountDetailsResponseWrapper responseWrapper = AccountDetailsResponseWrapper
                .builder()
                .messageId(messageId)
                .statusCode("0")
                .statusDescription("Success")
                .accounts(accountDetailsList)
                .build();

        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateAccountDetails(AccountDataRequest accountDataRequest) {

        try{
            Optional<AccountDataEntity> accountDataRequestEntity = accountDataEntityRepository.findByAccountId(accountDataRequest.getAccountId());


            if (accountDataRequestEntity.isPresent()) {


                accountDataRequestEntity.get().setStatus(accountDataRequest.getStatus());
                accountDataRequestEntity.get().setUpdatedAt(LocalDateTime.now());

                try{
                    accountDataEntityRepository.save(accountDataRequestEntity.get());

                    AccountDataResponse accountDataRequestResponse = AccountDataResponse
                            .builder()
                            .messageId(accountDataRequest.getMessageId())
                            .statusCode("0")
                            .statusDescription("Success")
                            .errorInfoList(null)
                            .accountId(accountDataRequestEntity.get().getAccountId())
                            .accountStatus(accountDataRequestEntity.get().getStatus())
                            .build();

                    return new ResponseEntity<>(accountDataRequestResponse, HttpStatus.OK);
                } catch (Exception e) {


                    log.error("EXCEPTION OCCURRED WHILE UPDATING CUSTOMER DATA ::: {}",e.getLocalizedMessage());

                    AccountDataResponse accountDataRequestResponse = AccountDataResponse
                            .builder()
                            .messageId(accountDataRequest.getMessageId())
                            .statusCode("1")
                            .statusDescription("Failure")
                            .errorInfoList(Collections.singletonList("Internal Server Error"))
                            .accountId(accountDataRequest.getAccountId())
                            .build();


                    return new ResponseEntity<>(accountDataRequestResponse, HttpStatus.INTERNAL_SERVER_ERROR);

                }
            }else{

                AccountDataResponse accountDataRequestResponse = AccountDataResponse
                        .builder()
                        .messageId(accountDataRequest.getMessageId())
                        .statusCode("1")
                        .statusDescription("Failure")
                        .errorInfoList(Collections.singletonList("Account does not exist in system"))
                        .accountId(accountDataRequest.getAccountId())
                        .build();


                return new ResponseEntity<>(accountDataRequestResponse, HttpStatus.NOT_FOUND);

            }


        }catch (Exception ex){
            log.error("EXCEPTION OCCURRED WHILE UPDATING ACCOUNT DATA ::: {}",ex.getLocalizedMessage());

            AccountDataResponse accountDataRequestResponse = AccountDataResponse
                    .builder()
                    .messageId(accountDataRequest.getMessageId())
                    .statusCode("1")
                    .statusDescription("Failure")
                    .errorInfoList(Collections.singletonList("Internal Server Error"))
                    .accountId(accountDataRequest.getAccountId())
                    .build();


            return new ResponseEntity<>(accountDataRequestResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> deleteAccountDetails(AccountDataRequest accountDataRequest) {
        try{
            Optional<AccountDataEntity> accountDataRequestEntity = accountDataEntityRepository.findByAccountId(accountDataRequest.getAccountId());


            if (accountDataRequestEntity.isPresent()) {


                accountDataRequestEntity.get().setStatus(ProcessEnums.INACTIVE.name());

                try{
                    accountDataEntityRepository.save(accountDataRequestEntity.get());

                    AccountDataResponse accountDataRequestResponse = AccountDataResponse
                            .builder()
                            .messageId(accountDataRequest.getMessageId())
                            .statusCode("0")
                            .statusDescription("Success")
                            .errorInfoList(null)
                            .accountId(accountDataRequestEntity.get().getAccountId())
                            .accountStatus(accountDataRequestEntity.get().getStatus())
                            .build();

                    return new ResponseEntity<>(accountDataRequestResponse, HttpStatus.OK);
                } catch (Exception e) {


                    log.error("EXCEPTION OCCURRED WHILE DELETING ACCOUNT DATA ::: {}",e.getLocalizedMessage());

                    AccountDataResponse accountDataRequestResponse = AccountDataResponse
                            .builder()
                            .messageId(accountDataRequest.getMessageId())
                            .statusCode("1")
                            .statusDescription("Failure")
                            .errorInfoList(Collections.singletonList("Internal Server Error"))
                            .accountId(accountDataRequest.getAccountId())
                            .build();


                    return new ResponseEntity<>(accountDataRequestResponse, HttpStatus.INTERNAL_SERVER_ERROR);

                }
            }else{




                AccountDataResponse accountDataRequestResponse = AccountDataResponse
                        .builder()
                        .messageId(accountDataRequest.getMessageId())
                        .statusCode("1")
                        .statusDescription("Failure")
                        .errorInfoList(Collections.singletonList("Account does not exist in system"))
                        .accountId(accountDataRequest.getAccountId())
                        .build();


                return new ResponseEntity<>(accountDataRequestResponse, HttpStatus.NOT_FOUND);

            }


        }catch (Exception ex){
            log.error("EXCEPTION OCCURRED WHILE DELETING ACCOUNT DATA ::: {}",ex.getLocalizedMessage());

            AccountDataResponse accountDataRequestResponse = AccountDataResponse
                    .builder()
                    .messageId(accountDataRequest.getMessageId())
                    .statusCode("1")
                    .statusDescription("Failure")
                    .errorInfoList(Collections.singletonList("Internal Server Error"))
                    .accountId(accountDataRequest.getAccountId())
                    .build();


            return new ResponseEntity<>(accountDataRequestResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
