package com.karenkipchirchir.main.utils;

import com.karenkipchirchir.main.models.CardTypes;
import com.karenkipchirchir.main.models.database.AccountDataEntity;
import com.karenkipchirchir.main.models.database.CardsEntity;
import com.karenkipchirchir.main.models.request.CardDataRequest;
import com.karenkipchirchir.main.repo.AccountDataEntityRepository;
import com.karenkipchirchir.main.repo.CardsEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@Slf4j
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "properties.idempotencyCheck=5")
class RequestValidationTest {

    @InjectMocks
    private RequestValidation requestValidation;

    @Mock
    private Utils utils;

    @Value("${properties.idempotencyCheck}")
    public Integer lastCheckTime;

    @Value("${properties.maxCards}")
    public Integer maxCards;

    @Mock
    private AccountDataEntityRepository accountDataEntityRepository;
    @Mock
    private CardsEntityRepository cardsEntityRepository;

    @BeforeEach
    void setUp() {
        // Set max cards and last check time
        requestValidation.maxCards = 1;
        requestValidation.lastCheckTime = 5;
    }

    @Test
    void testValidateCardRequestCreateValidRequest() {

        CardDataRequest request = new CardDataRequest();
        request.setAccountId(123);
        request.setCardAlias("Test Card");
        request.setCardType(CardTypes.PHYSICAL);
        request.setMessageId("UUID");

        AccountDataEntity account = new AccountDataEntity();
        CardsEntity cardsEntity = new CardsEntity();
        Mockito.when(accountDataEntityRepository.findByAccountId(123)).thenReturn(Optional.of(account));
        Mockito.when(cardsEntityRepository.findByAccountId_AccountId(123)).thenReturn(Collections.emptyList());
        Mockito.when(utils.generateRequestHash(anyString(), any())).thenReturn("HASH");;
        Mockito.when(cardsEntityRepository.findByRequestHashAndCreatedAtAfter(any(), any()))
                .thenReturn(Optional.empty());


        CardDataRequest result = requestValidation.validateCardRequest(request, "CREATE");

        assertTrue(result.getIsValid());
        assertNull(result.getErrorMessages());
        assertEquals("HASH", result.getRequestHash());
    }


    @Test
    void testValidateCardRequestCreateInvalidRequest() {

        CardDataRequest result = requestValidation.validateCardRequest(null, "CREATE");

        assertFalse(result.getIsValid());
        assertNotNull(result.getErrorMessages());

    }


    @Test
    void testValidateCardRequestCreateInvalidRequestMissingMessageID() {

        CardDataRequest request = new CardDataRequest();
        request.setAccountId(123);
        request.setCardAlias("Test Card");
        request.setCardType(CardTypes.PHYSICAL);

        CardDataRequest result = requestValidation.validateCardRequest(request, "CREATE");

        assertFalse(result.getIsValid());
        assertNotNull(result.getErrorMessages());

    }

    @Test
    void testValidateCardRequestCreateInvalidRequestMissingAccountID() {

        CardDataRequest request = new CardDataRequest();
        request.setMessageId("UUID");
        request.setCardAlias("Test Card");
        request.setCardType(CardTypes.PHYSICAL);

        Mockito.when(accountDataEntityRepository.findByAccountId(123)).thenReturn(Optional.empty());
        Mockito.when(cardsEntityRepository.findByAccountId_AccountId(123)).thenReturn(Collections.emptyList());
        Mockito.when(cardsEntityRepository.findByRequestHashAndCreatedAtAfter(any(), any()))
                .thenReturn(Optional.empty());

        CardDataRequest result = requestValidation.validateCardRequest(request, "CREATE");

        assertFalse(result.getIsValid());
        assertNotNull(result.getErrorMessages());

    }


    @Test
    void  testValidateCardRequestNullAccountId(){
        CardDataRequest request = new CardDataRequest();
        request.setMessageId("uuid");
        request.setCardAlias("TEST CARD");
        request.setCardType(CardTypes.PHYSICAL);


        CardDataRequest result = requestValidation.validateCardRequest(request, "CREATE");

        assertFalse(result.getIsValid());
        assertNotNull(result.getErrorMessages());
        log.info(result.getErrorMessages().toString());
        assertTrue(result.getErrorMessages().contains("Account ID does not exist in system"));
    }


    @Test
    void  testValidateCardRequestNullCardAlias(){
        CardDataRequest request = new CardDataRequest();
        request.setMessageId("uuid");
        request.setAccountId(123);
        request.setCardAlias(null);
        request.setCardType(CardTypes.PHYSICAL);


        Mockito.when(accountDataEntityRepository.findByAccountId(123)).thenReturn(Optional.empty());
        Mockito.when(cardsEntityRepository.findByAccountId_AccountId(123)).thenReturn(Collections.emptyList());


        CardDataRequest result = requestValidation.validateCardRequest(request, "CREATE");

        assertFalse(result.getIsValid());
        assertNotNull(result.getErrorMessages());
    }



    @Test
    void  testValidateCardRequestNullCardType(){
        CardDataRequest request = new CardDataRequest();
        request.setMessageId("uuid");
        request.setAccountId(123);
        request.setCardAlias("Test Alias");
        request.setCardType(null);


        Mockito.when(accountDataEntityRepository.findByAccountId(123)).thenReturn(Optional.empty());
        Mockito.when(cardsEntityRepository.findByAccountId_AccountId(123)).thenReturn(Collections.emptyList());

        CardDataRequest result = requestValidation.validateCardRequest(request, "CREATE");

        assertFalse(result.getIsValid());
        assertNotNull(result.getErrorMessages());
    }



    @Test
    void  testValidateCardRequestMaximumNumberOfCardsReached(){
        CardDataRequest request = new CardDataRequest();
        request.setMessageId("uuid");
        request.setAccountId(123);
        request.setCardAlias("Test Alias");
        request.setCardType(CardTypes.PHYSICAL);

        List<CardsEntity> cardsEntityList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            CardsEntity card = new CardsEntity();
            card.setCardType(CardTypes.PHYSICAL);
            cardsEntityList.add(card);
        }

        AccountDataEntity accountDataEntity = new AccountDataEntity();
        accountDataEntity.setAccountId(123);

        Mockito.when(accountDataEntityRepository.findByAccountId(123)).thenReturn(Optional.of(accountDataEntity));
        Mockito.when(cardsEntityRepository.findByAccountId_AccountId(123)).thenReturn(cardsEntityList);

        CardDataRequest result = requestValidation.validateCardRequest(request, "CREATE");

        assertFalse(result.getIsValid());
        assertNotNull(result.getErrorMessages());
        assertTrue(result.getErrorMessages().contains("Maximum number of cards reached"));
    }

}
