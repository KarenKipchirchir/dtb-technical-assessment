package com.karenkipchirchir.main.controller;

import com.karenkipchirchir.main.models.request.CardDataRequest;
import com.karenkipchirchir.main.models.response.CardDataResponse;
import com.karenkipchirchir.main.service.CardDataInterface;
import com.karenkipchirchir.main.utils.RequestValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("api/v1/cards/")
public class CardDataController {

    @Autowired
    RequestValidation requestValidation;

    @Autowired
    CardDataInterface cardDataInterface;


    @PostMapping("create")
    public ResponseEntity<?> createCardRequest(@RequestBody CardDataRequest cardDataRequest){

        CardDataRequest request = requestValidation.validateCardRequest(cardDataRequest,"CREATE");


        if (request.getIsValid()){
            return cardDataInterface.processCreateCardRequest(cardDataRequest);
        }else {
            CardDataResponse cardDataRequestResponse = CardDataResponse
                    .builder()
                    .messageId(cardDataRequest.getMessageId())
                    .statusCode("1")
                    .statusDescription("Failure")
                    .errorInfoList(request.getErrorMessages())
                    .build();


            return new ResponseEntity<>(cardDataRequestResponse, HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("filter")
    public ResponseEntity<?> fetchCardDetails(@RequestParam String messageId,
                                                  @RequestParam String cardAlias,
                                                  @RequestParam String typeOfCard,
                                                  @RequestParam String pan,
                                                  @RequestParam boolean override,
                                                  @RequestParam Integer pageNumber,
                                                  @RequestParam Integer pageSize){

        return cardDataInterface.fetchCardDetails(messageId,cardAlias,typeOfCard,override,pan,pageNumber,pageSize);

    }

    @PutMapping("update")
    public ResponseEntity<?> updateCardDetails(@RequestBody CardDataRequest cardDataRequest){

        CardDataRequest request = requestValidation.validateCardRequest(cardDataRequest, "UPDATE");


        if (request.getIsValid()){
            return cardDataInterface.updateCardDetails(cardDataRequest);
        }else {
            CardDataResponse cardDataRequestResponse = CardDataResponse
                    .builder()
                    .messageId(cardDataRequest.getMessageId())
                    .statusCode("1")
                    .statusDescription("Failure")
                    .errorInfoList(request.getErrorMessages())
                    .cardId(cardDataRequest.getCardId())
                    .cardStatus(cardDataRequest.getStatus())
                    .build();


            return new ResponseEntity<>(cardDataRequestResponse, HttpStatus.BAD_REQUEST);
        }
    }



    @DeleteMapping("delete")
    public ResponseEntity<?> deleteCardDetails(@RequestBody CardDataRequest cardDataRequest){

        CardDataRequest request = requestValidation.validateCardRequest(cardDataRequest, "DELETE");


        if (request.getIsValid()){
            return cardDataInterface.deleteCardDetails(cardDataRequest);
        }else {
            CardDataResponse cardDataRequestResponse = CardDataResponse
                    .builder()
                    .messageId(cardDataRequest.getMessageId())
                    .statusCode("1")
                    .statusDescription("Failure")
                    .errorInfoList(request.getErrorMessages())
                    .cardId(cardDataRequest.getCardId())
                    .build();


            return new ResponseEntity<>(cardDataRequestResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
