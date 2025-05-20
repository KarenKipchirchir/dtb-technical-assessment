package com.karenkipchirchir.main.models.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBioDataRequest {

    private String messageId;
    private Integer customerId;
    private String firstName;
    private String lastName;
    private String otherName;
    private String status;

    @JsonIgnore
    private Boolean isValid = true;
    @JsonIgnore
    private List<String> errorMessages = null;
    @JsonIgnore
    private String requestHash;


}
