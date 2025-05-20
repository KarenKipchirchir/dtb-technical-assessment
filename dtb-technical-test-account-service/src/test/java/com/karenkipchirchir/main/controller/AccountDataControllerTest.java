package com.karenkipchirchir.main.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.karenkipchirchir.main.models.request.AccountDataRequest;
import com.karenkipchirchir.main.repo.AccountDataEntityRepository;
import com.karenkipchirchir.main.service.AccountDataInterface;
import com.karenkipchirchir.main.utils.RequestValidation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ExtendWith(MockitoExtension.class)
@WebMvcTest(AccountDataController.class)
class AccountDataControllerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }


    @Mock
    private AccountDataEntityRepository accountDataEntityRepository;

    @MockBean
    private RequestValidation requestValidation;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountDataInterface accountDataInterface;

    private final Gson gson = new Gson();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void createAccountRequest() throws Exception {

        AccountDataRequest accountDataRequest = new AccountDataRequest();
        accountDataRequest.setMessageId("uuid");
        accountDataRequest.setAccountId(123);
        accountDataRequest.setBranchCode(420);
        accountDataRequest.setIsValid(true);



        Mockito.when(requestValidation.validateAccountRequest(Mockito.any(), Mockito.eq("CREATE")))
                .thenReturn(accountDataRequest);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDataRequest)))
                .andExpect(status().isOk());


    }


    @Test
    void createAccountRequestInvalid() throws Exception {

        AccountDataRequest accountDataRequest = null;

        Mockito.when(requestValidation.validateAccountRequest(accountDataRequest,"CREATE")).thenReturn(accountDataRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(accountDataRequest)))
                .andExpect(status().is4xxClientError());


    }

    @Test
    void createAccountRequestValidationFailed() throws Exception {

        AccountDataRequest accountDataRequest = new AccountDataRequest();
        accountDataRequest.setMessageId("uuid");
        accountDataRequest.setAccountId(123);
        accountDataRequest.setBranchCode(420);
        accountDataRequest.setIsValid(false);

        Mockito.when(requestValidation.validateAccountRequest(Mockito.any(), Mockito.eq("CREATE")))
                .thenReturn(accountDataRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDataRequest)))
                .andExpect(status().is4xxClientError());


    }

    @Test
    void createAccountRequestDBException() throws Exception {

        AccountDataRequest accountDataRequest = new AccountDataRequest();
        accountDataRequest.setIsValid(true);



        Mockito.when(requestValidation.validateAccountRequest(accountDataRequest,"CREATE")).thenReturn(accountDataRequest);
        Mockito.when(accountDataInterface.processCreateAccountRequest(accountDataRequest))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDataRequest)))
                .andExpect(status().is5xxServerError());


    }


    @Test
    void createAccountRequestNullAccountId() throws Exception {

        AccountDataRequest accountDataRequest = new AccountDataRequest();
        accountDataRequest.setMessageId("uuid");
        accountDataRequest.setAccountId(null);
        accountDataRequest.setBranchCode(420);



        Mockito.when(requestValidation.validateAccountRequest(accountDataRequest, "CREATE")).thenReturn(accountDataRequest);
        Mockito.when(accountDataInterface.processCreateAccountRequest(accountDataRequest))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDataRequest)))
                .andExpect(status().is5xxServerError());

    }


    @Test
    void fetchAccountDetails() throws Exception {

        String messageId = "uuid";
        String iban = "qwerty";
        String bicSwift = "qwerty";
        String cardAlias = "qwerty";
        int pageNumber = 0;
        int pageSize = 10;



        Mockito.when(accountDataInterface.fetchAccountDetails(messageId,iban,bicSwift,cardAlias,pageNumber,pageSize))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));


        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/account/filter")
                        .param("messageId", messageId)
                        .param("iban", iban)
                        .param("bicSwift", bicSwift)
                        .param("cardAlias", cardAlias)
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize)))
                .andExpect(status().isOk());
    }





    @Test
    void updateAccountDetails() throws Exception {

        AccountDataRequest accountDataRequest = new AccountDataRequest();
        accountDataRequest.setMessageId("uuid");
        accountDataRequest.setAccountId(123);
        accountDataRequest.setBranchCode(420);
        accountDataRequest.setStatus("INACTIVE");


        Mockito.when(requestValidation.validateAccountRequest(Mockito.any(), Mockito.eq("UPDATE")))
                .thenReturn(accountDataRequest);


        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/account/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDataRequest)))
                .andExpect(status().isOk());

    }

    @Test
    void updateAccountDetailsNullRequest() throws Exception {

        AccountDataRequest accountDataRequest = null;


        Mockito.when(requestValidation.validateAccountRequest(Mockito.any(), Mockito.eq("UPDATE")))
                .thenReturn(accountDataRequest);


        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/account/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(accountDataRequest)))
                .andExpect(status().isBadRequest());

    }


    @Test
    void updateAccountDetailsNullMessageId() throws Exception {

        AccountDataRequest accountDataRequest = new AccountDataRequest();
        accountDataRequest.setAccountId(null);
        accountDataRequest.setMessageId(null);
        accountDataRequest.setIsValid(false);


        Mockito.when(requestValidation.validateAccountRequest(Mockito.any(), Mockito.eq("UPDATE")))
                .thenReturn(accountDataRequest);


        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/account/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDataRequest)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void deleteAccountDetails() throws Exception {

        AccountDataRequest accountDataRequest = new AccountDataRequest();
        accountDataRequest.setMessageId("uuid");
        accountDataRequest.setAccountId(123);


        Mockito.when(requestValidation.validateAccountRequest(Mockito.any(), Mockito.eq("DELETE")))
                .thenReturn(accountDataRequest);


        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/account/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDataRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAccountDetailsNullRequest() throws Exception {

        AccountDataRequest accountDataRequest = null;


        Mockito.when(requestValidation.validateAccountRequest(Mockito.any(), Mockito.eq("DELETE")))
                .thenReturn(accountDataRequest);


        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/account/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(accountDataRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAccountDetailsNullAccountId() throws Exception {

        AccountDataRequest accountDataRequest = new AccountDataRequest();
        accountDataRequest.setAccountId(null);
        accountDataRequest.setIsValid(false);


        Mockito.when(requestValidation.validateAccountRequest(Mockito.any(), Mockito.eq("DELETE")))
                .thenReturn(accountDataRequest);


        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/account/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDataRequest)))
                .andExpect(status().isBadRequest());
    }
    
    
}