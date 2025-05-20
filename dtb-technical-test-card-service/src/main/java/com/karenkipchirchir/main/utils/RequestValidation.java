package com.karenkipchirchir.main.utils;

import com.karenkipchirchir.main.models.CardTypes;
import com.karenkipchirchir.main.models.ProcessEnums;
import com.karenkipchirchir.main.models.database.AccountDataEntity;
import com.karenkipchirchir.main.models.database.CardsEntity;
import com.karenkipchirchir.main.models.request.CardDataRequest;
import com.karenkipchirchir.main.repo.AccountDataEntityRepository;
import com.karenkipchirchir.main.repo.BranchCodesEntityRepository;
import com.karenkipchirchir.main.repo.CardsEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RequestValidation {

    @Autowired
    Utils utils;

    @Value("${properties.idempotencyCheck}")
    public Integer lastCheckTime;

    @Value("${properties.maxCards}")
    public Integer maxCards;

    @Autowired
    private AccountDataEntityRepository accountDataEntityRepository;
    @Autowired
    private CardsEntityRepository cardsEntityRepository;

    public CardDataRequest validateCardRequest(CardDataRequest cardDataRequest, String requestType) {


        List<String> errorMessages = new ArrayList<>();
        String requestHash = null;

        if (requestType.equalsIgnoreCase("CREATE")) {

            if (cardDataRequest == null) {
                errorMessages.add("Request cannot be null");
                cardDataRequest = new CardDataRequest();
            } else {


                if (checkIfStringIsValid(cardDataRequest.getMessageId())) {
                    errorMessages.add("Message ID cannot be null/empty");
                }

                try {
                    String accountId = String.valueOf(cardDataRequest.getAccountId());
                    if (checkIfStringIsValid(accountId)) {
                        errorMessages.add("Account ID cannot be null/empty");
                    } else {

                        try {

                            Optional<AccountDataEntity> accountDataEntity = accountDataEntityRepository.findByAccountId(cardDataRequest.getAccountId());

                            if (accountDataEntity.isEmpty()) {
                                errorMessages.add("Account ID does not exist in system");
                            }else{
                                cardDataRequest.setAccountDataEntity(accountDataEntity.get());
                            }

                        } catch (Exception e) {
                            errorMessages.add("Account ID validation failed");
                        }

                    }

                } catch (Exception ex) {
                    errorMessages.add("Account ID must be a numeric value");
                }


                if (checkIfStringIsValid(cardDataRequest.getCardAlias())) {
                    errorMessages.add("Card Alias cannot be null/empty");
                }

                if (checkIfStringIsValid(String.valueOf(cardDataRequest.getCardType()))) {
                    errorMessages.add("Card Type cannot be null/empty");
                }else {
                    if (!CardTypes.PHYSICAL.name().equalsIgnoreCase(String.valueOf(cardDataRequest.getCardType()))  && !CardTypes.VIRTUAL.name().equalsIgnoreCase(String.valueOf(cardDataRequest.getCardType()))){
                        errorMessages.add("Invalid Card Type passed");
                    }
                }


                try {

                    List<CardsEntity> cardsEntityList = cardsEntityRepository.findByAccountId_AccountId(cardDataRequest.getAccountId());

                    if (cardsEntityList.size() >= maxCards) {
                        errorMessages.add("Maximum number of cards reached");
                    }else{

                        for (CardsEntity card : cardsEntityList){

                            if (card.getCardType().equals(cardDataRequest.getCardType())){
                                errorMessages.add(cardDataRequest.getCardType()+" Card type already exists for this account");
                            }
                        }

                    }

                } catch (Exception e) {
                    errorMessages.add("Number of cards validation failed");
                }

                
            }


        }else if (requestType.equalsIgnoreCase("UPDATE")) {
            if (checkIfStringIsValid(cardDataRequest.getMessageId())) {
                errorMessages.add("Message ID cannot be null/empty");
            }


            if (checkIfStringIsValid(cardDataRequest.getStatus())) {
                errorMessages.add("Status cannot be null/empty");
            } else if (!cardDataRequest.getStatus().equalsIgnoreCase(ProcessEnums.ACTIVE.name()) && !cardDataRequest.getStatus().equalsIgnoreCase(ProcessEnums.INACTIVE.name())) {
                errorMessages.add("Invalid status passed");
            }

            try {
                String cardId = String.valueOf(cardDataRequest.getCardId());
                if (checkIfStringIsValid(cardId)) {
                    errorMessages.add("Card ID cannot be null/empty");
                }

            } catch (Exception ex) {
                errorMessages.add("Card ID must be a numeric value");
            }
        }else if (requestType.equalsIgnoreCase("DELETE")) {
            if (checkIfStringIsValid(cardDataRequest.getMessageId())) {
                errorMessages.add("Message ID cannot be null/empty");
            }

            try {
                String customerId = String.valueOf(cardDataRequest.getCardId());
                if (checkIfStringIsValid(customerId)) {
                    errorMessages.add("Card ID cannot be null/empty");
                }

            } catch (Exception ex) {
                errorMessages.add("Card ID must be a numeric value");
            }
        }


        if (errorMessages.isEmpty()){
            LocalDateTime createdAfter  = LocalDateTime.now().minusMinutes(lastCheckTime);

           requestHash = utils.generateRequestHash(cardDataRequest.getMessageId(), cardDataRequest.getCardId());

           try {
               Optional<CardsEntity> cardDataRequestEntity = cardsEntityRepository.findByRequestHashAndCreatedAtAfter(requestHash,createdAfter);

               if (cardDataRequestEntity.isPresent()) {
                   errorMessages.add("Idempotency conflict detected");
               }
           } catch (Exception e) {
               errorMessages.add("Idempotency validation failed");
           }
        }


        if (errorMessages.isEmpty()){
            cardDataRequest.setIsValid(true);
            cardDataRequest.setRequestHash(requestHash);
        }else{
            cardDataRequest.setErrorMessages(errorMessages);
            cardDataRequest.setIsValid(false);
        }

        return cardDataRequest;
    }


    private Boolean checkIfStringIsValid(String string) {
        return string == null || string.trim().isEmpty();
    }
}
