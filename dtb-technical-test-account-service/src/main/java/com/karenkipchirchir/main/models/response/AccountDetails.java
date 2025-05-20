package com.karenkipchirchir.main.models.response;

import com.karenkipchirchir.main.models.CardTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDetails {
    private Integer accountId;
    private String iban;
    private String bicSwift;
    private String accountStatus;
    private String accountCurrency;
    private Integer accountBranchCode;
    private String customerNames;
    private Integer customerId;
    private CardTypes cardType;
}
