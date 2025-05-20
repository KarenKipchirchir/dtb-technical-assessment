package com.karenkipchirchir.main.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDataResponse {
    private String messageId;
    private String statusCode;
    private String statusDescription;
    private List<String> errorInfoList;
    private Integer accountId;
    private String iban;
    private String bicSwift;
    private String accountStatus;
    private String accountCurrency;
    private Integer accountBranchCode;
    private String customerNames;
    private Integer customerId;




}
