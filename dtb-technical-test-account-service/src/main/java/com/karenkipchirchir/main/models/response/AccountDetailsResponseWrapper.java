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
public class AccountDetailsResponseWrapper {
    private String messageId;
    private String statusCode;
    private String statusDescription;
    private List<AccountDetails> accounts;

}
