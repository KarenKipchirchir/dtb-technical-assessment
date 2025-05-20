package com.karenkipchirchir.main.service.impl;

import com.google.gson.Gson;
import com.karenkipchirchir.main.models.CardTypes;
import com.karenkipchirchir.main.models.database.CardsEntity;
import com.karenkipchirchir.main.models.request.CardDataRequest;
import com.karenkipchirchir.main.models.response.CardDetailsResponseWrapper;
import com.karenkipchirchir.main.repo.CardsEntityRepository;
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
class CardDataServiceTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }


    @Mock
    private CardsEntityRepository cardsEntityRepository;

    @Mock
    RequestValidation requestValidation;

    @InjectMocks
    private CardDataService cardDataService;

    @Mock
    Utils utils;


    private final Gson gson = new Gson();


    @Test
    void processCreateCardRequest() {
        CardDataRequest cardDataRequest = new CardDataRequest();
        cardDataRequest.setMessageId("uuid");
        cardDataRequest.setCardId(123);
        cardDataRequest.setCardAlias("Test");
        cardDataRequest.setCardType(CardTypes.VIRTUAL);
        cardDataRequest.setIsValid(true);

        ResponseEntity<?> result = cardDataService.processCreateCardRequest(cardDataRequest);

        assertTrue(result.getStatusCode().is2xxSuccessful());

    }


    @Test
    void processCreateCardRequestInternalServerError() {
        CardDataRequest cardDataRequest = new CardDataRequest();
        cardDataRequest.setMessageId("uuid");
        cardDataRequest.setCardId(123);
        cardDataRequest.setCardAlias("Test");
        cardDataRequest.setIsValid(true);


        Mockito.when(cardsEntityRepository.save(any())).thenThrow(new RuntimeException("DB Error"));

        ResponseEntity<?> result = cardDataService.processCreateCardRequest(cardDataRequest);

        assertTrue(result.getStatusCode().is5xxServerError());
    }


    @Test
    void processFetchCardRequest() {
        String messageId = "uuid";
        String cardAlias = "Test";
        String typeOfCard = CardTypes.PHYSICAL.name();
        boolean override = false;
        String pan = "1234567823451234523454567";
        int pageNumber = 0;
        int pageSize = 10;


        ResponseEntity<?> result = cardDataService.fetchCardDetails(messageId,cardAlias,typeOfCard,override,pan,pageNumber,pageSize);

        assertTrue(result.getStatusCode().is2xxSuccessful());

    }




    @Test
    void processFetchCardRequestNullCardAlias() {

        String messageId = "uuid";
        String cardAlias = "Test";
        String typeOfCard = CardTypes.PHYSICAL.name();
        boolean override = false;
        String pan = "1234567823451234523454567";
        int pageNumber = 0;
        int pageSize = 10;


        ResponseEntity<?> result = cardDataService.fetchCardDetails(messageId,cardAlias,typeOfCard,override,pan,pageNumber,pageSize);


        assertTrue(result.getStatusCode().is2xxSuccessful());
        String body = gson.toJson(result.getBody());
        CardDetailsResponseWrapper cardDetailsResponseWrapper = gson.fromJson(Objects.requireNonNull(body),CardDetailsResponseWrapper.class);
        log.info(gson.toJson(cardDetailsResponseWrapper));
        assertTrue(cardDetailsResponseWrapper.getCards().isEmpty());

    }

    @Test
    void updateCardDetails() {

        CardDataRequest accountDataRequest = new CardDataRequest();
        accountDataRequest.setMessageId("uuid");
        accountDataRequest.setCardId(123);
        accountDataRequest.setStatus("INACTIVE");
        accountDataRequest.setIsValid(true);


        Mockito.when(cardsEntityRepository.findByCardId(accountDataRequest.getCardId())).thenReturn(Optional.of(new CardsEntity()));

        ResponseEntity<?> result = cardDataService.updateCardDetails(accountDataRequest);

        log.info(result.getStatusCode().toString());
        log.info(gson.toJson(result.getBody()));

        assertTrue(result.getStatusCode().is2xxSuccessful());

    }


    @Test
    void updateCardDetailsNotFound() {

        CardDataRequest accountDataRequest = new CardDataRequest();
        accountDataRequest.setMessageId("uuid");
        accountDataRequest.setCardId(123);
        accountDataRequest.setStatus("INACTIVE");
        accountDataRequest.setIsValid(true);


        Mockito.when(cardsEntityRepository.findByCardId(accountDataRequest.getCardId()))
                .thenReturn(Optional.empty());


        ResponseEntity<?> result = cardDataService.updateCardDetails(accountDataRequest);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

    }

    @Test
    void deleteCardDetails() {

        CardDataRequest accountDataRequest = new CardDataRequest();
        accountDataRequest.setMessageId("uuid");
        accountDataRequest.setCardId(123);
        accountDataRequest.setIsValid(true);


        Mockito.when(cardsEntityRepository.findByCardId(accountDataRequest.getCardId()))
                .thenReturn(Optional.of(new CardsEntity()));


        ResponseEntity<?> result = cardDataService.deleteCardDetails(accountDataRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());

    }


    @Test
    void deleteCardDetailsNotFound() {

        CardDataRequest accountDataRequest = new CardDataRequest();
        accountDataRequest.setMessageId("uuid");
        accountDataRequest.setCardId(123);
        accountDataRequest.setIsValid(true);

        Mockito.when(cardsEntityRepository.findByCardId(accountDataRequest.getCardId()))
                .thenReturn(Optional.empty());


        ResponseEntity<?> result = cardDataService.deleteCardDetails(accountDataRequest);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

    }



}