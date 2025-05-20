package com.karenkipchirchir.main.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.karenkipchirchir.main.models.CardTypes;
import com.karenkipchirchir.main.models.request.CardDataRequest;
import com.karenkipchirchir.main.service.CardDataInterface;
import com.karenkipchirchir.main.utils.RequestValidation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
@WebMvcTest(CardDataController.class)
class CardDataControllerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }
    

    @MockBean
    private RequestValidation requestValidation;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardDataInterface cardDataInterface;

    private final Gson gson = new Gson();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void createCardRequest() throws Exception {

        CardDataRequest cardDataRequest = new CardDataRequest();
        cardDataRequest.setMessageId("uuid");
        cardDataRequest.setCardId(123);
        cardDataRequest.setCardAlias("Test");
        cardDataRequest.setCardType(CardTypes.VIRTUAL);
        cardDataRequest.setIsValid(true);



        Mockito.when(requestValidation.validateCardRequest(Mockito.any(), Mockito.eq("CREATE")))
                .thenReturn(cardDataRequest);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cards/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDataRequest)))
                .andExpect(status().isOk());


    }


    @Test
    void createCardRequestInvalid() throws Exception {

        CardDataRequest cardDataRequest = null;

        Mockito.when(requestValidation.validateCardRequest(cardDataRequest,"CREATE")).thenReturn(cardDataRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/cards/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(cardDataRequest)))
                .andExpect(status().is4xxClientError());


    }

    @Test
    void createCardRequestValidationFailed() throws Exception {

        CardDataRequest cardDataRequest = new CardDataRequest();
        cardDataRequest.setMessageId("uuid");
        cardDataRequest.setCardId(123);
        cardDataRequest.setCardAlias("Test");
        cardDataRequest.setIsValid(false);

        Mockito.when(requestValidation.validateCardRequest(Mockito.any(), Mockito.eq("CREATE")))
                .thenReturn(cardDataRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/cards/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDataRequest)))
                .andExpect(status().is4xxClientError());


    }

    @Test
    void createCardRequestDBException() throws Exception {

        CardDataRequest cardDataRequest = new CardDataRequest();
        cardDataRequest.setIsValid(true);



        Mockito.when(requestValidation.validateCardRequest(cardDataRequest,"CREATE")).thenReturn(cardDataRequest);
        Mockito.when(cardDataInterface.processCreateCardRequest(cardDataRequest))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/cards/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDataRequest)))
                .andExpect(status().is5xxServerError());


    }


    @Test
    void createCardRequestNullCardId() throws Exception {

        CardDataRequest cardDataRequest = new CardDataRequest();
        cardDataRequest.setMessageId("uuid");
        cardDataRequest.setCardId(null);
        cardDataRequest.setCardType(CardTypes.PHYSICAL);



        Mockito.when(requestValidation.validateCardRequest(cardDataRequest, "CREATE")).thenReturn(cardDataRequest);
        Mockito.when(cardDataInterface.processCreateCardRequest(cardDataRequest))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/cards/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDataRequest)))
                .andExpect(status().is5xxServerError());

    }


    @Test
    void fetchCardDetails() throws Exception {

        String messageId = "uuid";
        String cardAlias = "Test";
        String typeOfCard = CardTypes.PHYSICAL.name();
        boolean override = false;
        String pan = "1234567823451234523454567";
        int pageNumber = 0;
        int pageSize = 10;



        Mockito.when(cardDataInterface.fetchCardDetails(messageId,cardAlias,typeOfCard,override,pan,pageNumber,pageSize))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));


        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/cards/filter")
                        .param("messageId", messageId)
                        .param("cardAlias", cardAlias)
                        .param("typeOfCard", typeOfCard)
                        .param("override", String.valueOf(override))
                        .param("pan", pan)
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize)))
                .andExpect(status().isOk());
    }





    @Test
    void updateCardDetails() throws Exception {

        CardDataRequest cardDataRequest = new CardDataRequest();
        cardDataRequest.setMessageId("uuid");
        cardDataRequest.setCardId(123);
        cardDataRequest.setCardAlias("Test");
        cardDataRequest.setStatus("INACTIVE");


        Mockito.when(requestValidation.validateCardRequest(Mockito.any(), Mockito.eq("UPDATE")))
                .thenReturn(cardDataRequest);


        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/cards/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDataRequest)))
                .andExpect(status().isOk());

    }

    @Test
    void updateCardDetailsNullRequest() throws Exception {

        CardDataRequest cardDataRequest = null;


        Mockito.when(requestValidation.validateCardRequest(Mockito.any(), Mockito.eq("UPDATE")))
                .thenReturn(cardDataRequest);


        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/cards/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(cardDataRequest)))
                .andExpect(status().isBadRequest());

    }


    @Test
    void updateCardDetailsNullMessageId() throws Exception {

        CardDataRequest cardDataRequest = new CardDataRequest();
        cardDataRequest.setCardId(null);
        cardDataRequest.setMessageId(null);
        cardDataRequest.setIsValid(false);


        Mockito.when(requestValidation.validateCardRequest(Mockito.any(), Mockito.eq("UPDATE")))
                .thenReturn(cardDataRequest);


        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/cards/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDataRequest)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void deleteCardDetails() throws Exception {

        CardDataRequest cardDataRequest = new CardDataRequest();
        cardDataRequest.setMessageId("uuid");
        cardDataRequest.setCardId(123);


        Mockito.when(requestValidation.validateCardRequest(Mockito.any(), Mockito.eq("DELETE")))
                .thenReturn(cardDataRequest);


        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cards/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDataRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCardDetailsNullRequest() throws Exception {

        CardDataRequest cardDataRequest = null;


        Mockito.when(requestValidation.validateCardRequest(Mockito.any(), Mockito.eq("DELETE")))
                .thenReturn(cardDataRequest);


        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cards/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(cardDataRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCardDetailsNullCardId() throws Exception {

        CardDataRequest cardDataRequest = new CardDataRequest();
        cardDataRequest.setCardId(null);
        cardDataRequest.setIsValid(false);


        Mockito.when(requestValidation.validateCardRequest(Mockito.any(), Mockito.eq("DELETE")))
                .thenReturn(cardDataRequest);


        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cards/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDataRequest)))
                .andExpect(status().isBadRequest());
    }


}