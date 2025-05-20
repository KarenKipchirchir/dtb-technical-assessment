package com.karenkipchirchir.main.models.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.karenkipchirchir.main.models.CardTypes;
import com.karenkipchirchir.main.models.database.AccountDataEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Stack;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDataRequest {

    private String messageId;
    private Integer cardId;
    private String cardAlias;
    private Integer accountId;
    private CardTypes cardType;
    private String status;

    @JsonIgnore
    private Boolean isValid = true;
    @JsonIgnore
    private List<String> errorMessages = null;
    @JsonIgnore
    private String requestHash;
    
    @JsonIgnore
    private AccountDataEntity accountDataEntity;



}
