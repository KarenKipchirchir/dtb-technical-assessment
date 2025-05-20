package com.karenkipchirchir.main.models.response;

import com.karenkipchirchir.main.models.CardTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDetails {
    private Integer cardId;
    private String cardAlias;
    private Integer accountId;
    private CardTypes cardType;
    private String cardStatus;
    private String pan;
    private String cvv;
    private String cardExpiryDate;
    private Date virtualCardCVVExpiryTime;

}
