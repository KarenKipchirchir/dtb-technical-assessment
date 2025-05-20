package com.karenkipchirchir.main.models.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.karenkipchirchir.main.models.CardTypes;
import com.karenkipchirchir.main.models.database.BranchCodesEntity;
import com.karenkipchirchir.main.models.database.CustomerBioDataEntity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDataRequest {

    private String messageId;
    private Integer accountId;
    private String iban;
    private String bicSwift;
    private Integer branchCode;
    private String currency;
    private String status;
    private Integer customerId;

    @JsonIgnore
    private Boolean isValid = true;
    @JsonIgnore
    private List<String> errorMessages = null;
    @JsonIgnore
    private String requestHash;
    
    @JsonIgnore
    private CustomerBioDataEntity customerBioData;
    
    @JsonIgnore
    private BranchCodesEntity branchCodesEntity;


}
