package com.karenkipchirchir.main.service.impl;

import com.karenkipchirchir.main.models.CardTypes;
import com.karenkipchirchir.main.models.ProcessEnums;
import com.karenkipchirchir.main.models.database.CardsEntity;
import com.karenkipchirchir.main.models.request.CardDataRequest;
import com.karenkipchirchir.main.models.response.CardDataResponse;
import com.karenkipchirchir.main.models.response.CardDetails;
import com.karenkipchirchir.main.models.response.CardDetailsResponseWrapper;
import com.karenkipchirchir.main.repo.CardsEntityRepository;
import com.karenkipchirchir.main.service.CardDataInterface;
import com.karenkipchirchir.main.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class CardDataService implements CardDataInterface {

    @Autowired
    private CardsEntityRepository cardsEntityRepository;

    @Autowired
    Utils utils;

    @Override
    public ResponseEntity<?> processCreateCardRequest(CardDataRequest cardDataRequest) {

        String pan = utils.generatePrimaryAccountNumber(cardDataRequest.getAccountId());

        String cvv = utils.generateCVV();

        Integer cardId = utils.generateCardNumber();

        Date cvvExpiryMinutes = utils.generateCVVExpiryTime();

        String cardExpiryDate = utils.generateExpiryDate();




        CardsEntity cardDataEntity = CardsEntity
                .builder()
                .cardId(cardId)
                .cardAlias(cardDataRequest.getCardAlias())
                .cardType(cardDataRequest.getCardType())
                .pan(pan)
                .cvv(cvv)
                .status(ProcessEnums.ACTIVE.name())
                .requestHash(cardDataRequest.getRequestHash())
                .createdAt(LocalDateTime.now())
                .cardExpiryDate(cardExpiryDate)
                .virtualCardCVVExpiryTime(cvvExpiryMinutes)
                .accountId(cardDataRequest.getAccountDataEntity())
                .build();



        try{
            cardsEntityRepository.save(cardDataEntity);

            CardDataResponse cardDataRequestResponse = new CardDataResponse();

            if (cardDataRequest.getCardType().equals(CardTypes.VIRTUAL)){
                cardDataRequestResponse.setVirtualCardCVVExpiryTime(cvvExpiryMinutes);
            }


            cardDataRequestResponse.setMessageId(cardDataRequest.getMessageId());
            cardDataRequestResponse.setStatusCode("0");
            cardDataRequestResponse.setStatusDescription("Success");
            cardDataRequestResponse.setErrorInfoList(null);
            cardDataRequestResponse.setCardId(cardId);
            cardDataRequestResponse.setCardAlias(cardDataRequest.getCardAlias());
            cardDataRequestResponse.setCardType(cardDataRequest.getCardType());
            cardDataRequestResponse.setCardStatus(ProcessEnums.ACTIVE.name());
            cardDataRequestResponse.setCardExpiryDate(cardExpiryDate);



            return new ResponseEntity<>(cardDataRequestResponse, HttpStatus.OK);
        }catch (Exception ex){
            log.error("EXCEPTION OCCURRED WHILE SAVING CARD DATA ::: {}",ex.getLocalizedMessage());

            CardDataResponse cardDataRequestResponse = CardDataResponse
                    .builder()
                    .messageId(cardDataRequest.getMessageId())
                    .statusCode("1")
                    .statusDescription("Failure")
                    .errorInfoList(Collections.singletonList("Internal Server Error"))
                    .build();


            return new ResponseEntity<>(cardDataRequestResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public ResponseEntity<?> fetchCardDetails(String messageId, String cardAlias, String typeOfCard, boolean override, String pan, Integer pageNumber, Integer pageSize) {
        List<CardDetails> cardDetailsList = new ArrayList<>();

        Pageable paging = PageRequest.of(pageNumber, pageSize);

        List<CardsEntity> cardDataEntityList = cardsEntityRepository.findByCardAliasLikeOrCardAliasContainsAndCardTypeAndPan(cardAlias,cardAlias, CardTypes.valueOf(typeOfCard),pan,paging);

        for(CardsEntity cardDataEntity : cardDataEntityList) {

            CardDetails response = new CardDetails();
            if (cardDataEntity.getCardType().equals(CardTypes.VIRTUAL)) {
                Date cvvExpiryMinutes = utils.generateCVVExpiryTime();
                response.setVirtualCardCVVExpiryTime(cvvExpiryMinutes);
                cardsEntityRepository.save(cardDataEntity);

            }

            response.setCardId(cardDataEntity.getCardId());
            response.setCardAlias(cardDataEntity.getCardAlias());
            response.setCardType(cardDataEntity.getCardType());
            response.setCardStatus(cardDataEntity.getStatus());
            response.setCardExpiryDate(cardDataEntity.getCardExpiryDate());


            if (override) {
                response.setCvv(cardDataEntity.getCvv());
                response.setPan(cardDataEntity.getPan());

            }else {

                response.setCvv(utils.maskCvv());
                response.setPan(utils.maskPan(cardDataEntity.getPan()));
            }


            cardDetailsList.add(response);

        }


        CardDetailsResponseWrapper responseWrapper = CardDetailsResponseWrapper
                .builder()
                .messageId(messageId)
                .statusCode("0")
                .statusDescription("Success")
                .cards(cardDetailsList)
                .build();

        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> updateCardDetails(CardDataRequest cardDataRequest) {

        try{
            Optional<CardsEntity> cardDataRequestEntity = cardsEntityRepository.findByCardId(cardDataRequest.getCardId());


            if (cardDataRequestEntity.isPresent()) {

                if (cardDataRequest.getCardAlias() != null && !cardDataRequest.getCardAlias().isEmpty()){
                    cardDataRequestEntity.get().setCardAlias(cardDataRequest.getCardAlias());
                }


                cardDataRequestEntity.get().setStatus(cardDataRequest.getStatus());
                cardDataRequestEntity.get().setUpdatedAt(LocalDateTime.now());

                try{
                    cardsEntityRepository.save(cardDataRequestEntity.get());

                    CardDataResponse cardDataRequestResponse = CardDataResponse
                            .builder()
                            .messageId(cardDataRequest.getMessageId())
                            .statusCode("0")
                            .statusDescription("Success")
                            .errorInfoList(null)
                            .cardId(cardDataRequestEntity.get().getCardId())
                            .cardStatus(cardDataRequestEntity.get().getStatus())
                            .cardAlias(cardDataRequest.getCardAlias())
                            .cardType(cardDataRequestEntity.get().getCardType())
                            .cardExpiryDate(cardDataRequestEntity.get().getCardExpiryDate())
                            .build();

                    return new ResponseEntity<>(cardDataRequestResponse, HttpStatus.OK);
                } catch (Exception e) {


                    log.error("EXCEPTION OCCURRED WHILE UPDATING CUSTOMER DATA ::: {}",e.getLocalizedMessage());

                    CardDataResponse cardDataRequestResponse = CardDataResponse
                            .builder()
                            .messageId(cardDataRequest.getMessageId())
                            .statusCode("1")
                            .statusDescription("Failure")
                            .errorInfoList(Collections.singletonList("Internal Server Error"))
                            .cardId(cardDataRequest.getCardId())
                            .build();


                    return new ResponseEntity<>(cardDataRequestResponse, HttpStatus.INTERNAL_SERVER_ERROR);

                }
            }else{

                CardDataResponse cardDataRequestResponse = CardDataResponse
                        .builder()
                        .messageId(cardDataRequest.getMessageId())
                        .statusCode("1")
                        .statusDescription("Failure")
                        .errorInfoList(Collections.singletonList("Card does not exist in system"))
                        .cardId(cardDataRequest.getCardId())
                        .build();


                return new ResponseEntity<>(cardDataRequestResponse, HttpStatus.NOT_FOUND);

            }


        }catch (Exception ex){
            log.error("EXCEPTION OCCURRED WHILE UPDATING CARD DATA ::: {}",ex.getLocalizedMessage());

            CardDataResponse cardDataRequestResponse = CardDataResponse
                    .builder()
                    .messageId(cardDataRequest.getMessageId())
                    .statusCode("1")
                    .statusDescription("Failure")
                    .errorInfoList(Collections.singletonList("Internal Server Error"))
                    .cardId(cardDataRequest.getCardId())
                    .build();


            return new ResponseEntity<>(cardDataRequestResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> deleteCardDetails(CardDataRequest cardDataRequest) {
        try{
            Optional<CardsEntity> cardDataRequestEntity = cardsEntityRepository.findByCardId(cardDataRequest.getCardId());


            if (cardDataRequestEntity.isPresent()) {


                cardDataRequestEntity.get().setStatus(ProcessEnums.INACTIVE.name());

                try{
                    cardsEntityRepository.save(cardDataRequestEntity.get());

                    CardDataResponse cardDataRequestResponse = CardDataResponse
                            .builder()
                            .messageId(cardDataRequest.getMessageId())
                            .statusCode("0")
                            .statusDescription("Success")
                            .errorInfoList(null)
                            .cardId(cardDataRequestEntity.get().getCardId())
                            .cardStatus(cardDataRequestEntity.get().getStatus())
                            .build();

                    return new ResponseEntity<>(cardDataRequestResponse, HttpStatus.OK);
                } catch (Exception e) {


                    log.error("EXCEPTION OCCURRED WHILE DELETING CARD DATA ::: {}",e.getLocalizedMessage());

                    CardDataResponse cardDataRequestResponse = CardDataResponse
                            .builder()
                            .messageId(cardDataRequest.getMessageId())
                            .statusCode("1")
                            .statusDescription("Failure")
                            .errorInfoList(Collections.singletonList("Internal Server Error"))
                            .cardId(cardDataRequest.getCardId())
                            .build();


                    return new ResponseEntity<>(cardDataRequestResponse, HttpStatus.INTERNAL_SERVER_ERROR);

                }
            }else{




                CardDataResponse cardDataRequestResponse = CardDataResponse
                        .builder()
                        .messageId(cardDataRequest.getMessageId())
                        .statusCode("1")
                        .statusDescription("Failure")
                        .errorInfoList(Collections.singletonList("Card does not exist in system"))
                        .cardId(cardDataRequest.getCardId())
                        .build();


                return new ResponseEntity<>(cardDataRequestResponse, HttpStatus.NOT_FOUND);

            }


        }catch (Exception ex){
            log.error("EXCEPTION OCCURRED WHILE DELETING CARD DATA ::: {}",ex.getLocalizedMessage());

            CardDataResponse cardDataRequestResponse = CardDataResponse
                    .builder()
                    .messageId(cardDataRequest.getMessageId())
                    .statusCode("1")
                    .statusDescription("Failure")
                    .errorInfoList(Collections.singletonList("Internal Server Error"))
                    .cardId(cardDataRequest.getCardId())
                    .build();


            return new ResponseEntity<>(cardDataRequestResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
