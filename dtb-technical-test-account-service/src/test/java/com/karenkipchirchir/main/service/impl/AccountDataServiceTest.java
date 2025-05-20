package com.karenkipchirchir.main.service.impl;

import com.google.gson.Gson;
import com.karenkipchirchir.main.models.database.AccountDataEntity;
import com.karenkipchirchir.main.models.database.CustomerBioDataEntity;
import com.karenkipchirchir.main.models.request.AccountDataRequest;
import com.karenkipchirchir.main.models.response.AccountDetailsResponseWrapper;
import com.karenkipchirchir.main.repo.AccountDataEntityRepository;
import com.karenkipchirchir.main.utils.RequestValidation;
import com.karenkipchirchir.main.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AccountDataServiceTest {

    @Mock
    private AccountDataEntityRepository accountDataEntityRepository;

    @Mock
    RequestValidation requestValidation;

    @InjectMocks
    private AccountDataService accountDataService;

    @Mock
    Utils utils;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    private final Gson gson = new Gson();


    @Test
    void processCreateAccountRequest() {
        AccountDataRequest accountDataRequest = new AccountDataRequest();
        accountDataRequest.setMessageId("uuid");
        accountDataRequest.setAccountId(123);
        accountDataRequest.setBranchCode(420);
        accountDataRequest.setIsValid(true);

        CustomerBioDataEntity customerBioData = new CustomerBioDataEntity();
        customerBioData.setFirstName("KAREN");
        customerBioData.setLastName("KAREN");
        customerBioData.setOtherName("KAREN");

        accountDataRequest.setCustomerBioData(customerBioData);


        Mockito.when(utils.generateAccountNumber()).thenReturn(123);
        Mockito.when(utils.generateIBAN(any())).thenReturn("123");


        ResponseEntity<?> result = accountDataService.processCreateAccountRequest(accountDataRequest);

        assertTrue(result.getStatusCode().is2xxSuccessful());

        assertTrue(result.getStatusCode().is2xxSuccessful());
    }


    @Test
    void processCreateAccountRequestInternalServerError() {
        AccountDataRequest accountDataRequest = new AccountDataRequest();
        accountDataRequest.setMessageId("uuid");
        accountDataRequest.setAccountId(123);
        accountDataRequest.setBranchCode(420);
        accountDataRequest.setIsValid(true);


        Mockito.when(accountDataEntityRepository.save(any())).thenThrow(new RuntimeException("DB Error"));

        ResponseEntity<?> result = accountDataService.processCreateAccountRequest(accountDataRequest);

        assertTrue(result.getStatusCode().is5xxServerError());
    }


    @Test
    void processFetchAccountRequest() {
        String messageId = "uuid";
        String iban = "qwerty";
        String bicSwift = "qwerty";
        String cardAlias = "qwerty";
        int pageNumber = 0;
        int pageSize = 10;


        ResponseEntity<?> result = accountDataService.fetchAccountDetails(messageId,iban,bicSwift,cardAlias,pageNumber,pageSize);

        assertTrue(result.getStatusCode().is2xxSuccessful());

    }




    @Test
    void processFetchAccountRequestNullCardAlias() {
        String messageId = "uuid";
        String iban = "qwerty";
        String bicSwift = "qwerty";
        int pageNumber = 0;
        int pageSize = 10;


        ResponseEntity<?> result = accountDataService.fetchAccountDetails(messageId,iban,bicSwift,null,pageNumber,pageSize);


        assertTrue(result.getStatusCode().is2xxSuccessful());
        String body = gson.toJson(result.getBody());
        AccountDetailsResponseWrapper accountDetailsResponseWrapper = gson.fromJson(Objects.requireNonNull(body),AccountDetailsResponseWrapper.class);
        log.info(gson.toJson(accountDetailsResponseWrapper));
        assertTrue(accountDetailsResponseWrapper.getAccounts().isEmpty());

    }

    @Test
    void updateAccountDetails() {

        AccountDataRequest accountDataRequest = new AccountDataRequest();
        accountDataRequest.setMessageId("uuid");
        accountDataRequest.setAccountId(123);
        accountDataRequest.setStatus("INACTIVE");
        accountDataRequest.setIsValid(true);


        Mockito.when(accountDataEntityRepository.findByAccountId(accountDataRequest.getAccountId())).thenReturn(Optional.of(new AccountDataEntity()));

        ResponseEntity<?> result = accountDataService.updateAccountDetails(accountDataRequest);

        log.info(result.getStatusCode().toString());
        log.info(gson.toJson(result.getBody()));

        assertTrue(result.getStatusCode().is2xxSuccessful());

    }


    @Test
    void updateAccountDetailsNotFound() {

        AccountDataRequest accountDataRequest = new AccountDataRequest();
        accountDataRequest.setMessageId("uuid");
        accountDataRequest.setAccountId(123);
        accountDataRequest.setStatus("INACTIVE");
        accountDataRequest.setIsValid(true);


        Mockito.when(accountDataEntityRepository.findByAccountId(accountDataRequest.getAccountId()))
                .thenReturn(Optional.empty());


        ResponseEntity<?> result = accountDataService.updateAccountDetails(accountDataRequest);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

    }

    @Test
    void deleteAccountDetails() {

        AccountDataRequest accountDataRequest = new AccountDataRequest();
        accountDataRequest.setMessageId("uuid");
        accountDataRequest.setAccountId(123);
        accountDataRequest.setIsValid(true);


        Mockito.when(accountDataEntityRepository.findByAccountId(accountDataRequest.getAccountId()))
                .thenReturn(Optional.of(new AccountDataEntity()));


        ResponseEntity<?> result = accountDataService.deleteAccountDetails(accountDataRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());

    }


    @Test
    void deleteAccountDetailsNotFound() {

        AccountDataRequest accountDataRequest = new AccountDataRequest();
        accountDataRequest.setMessageId("uuid");
        accountDataRequest.setAccountId(123);
        accountDataRequest.setIsValid(true);

        Mockito.when(accountDataEntityRepository.findByAccountId(accountDataRequest.getAccountId()))
                .thenReturn(Optional.empty());


        ResponseEntity<?> result = accountDataService.deleteAccountDetails(accountDataRequest);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

    }
    
    
}