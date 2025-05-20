package com.karenkipchirchir.main.service;

import com.karenkipchirchir.main.models.request.CardDataRequest;
import org.springframework.http.ResponseEntity;

public interface CardDataInterface {
    ResponseEntity<?> processCreateCardRequest(CardDataRequest cardDataRequest);

    ResponseEntity<?> fetchCardDetails(String messageId, String cardAlias, String typeOfCard, boolean override, String pan, Integer pageNumber, Integer pageSize);

    ResponseEntity<?> updateCardDetails(CardDataRequest cardDataRequest);

    ResponseEntity<?> deleteCardDetails(CardDataRequest cardDataRequest);

}
