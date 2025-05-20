package com.karenkipchirchir.main.service;

import com.karenkipchirchir.main.models.request.AccountDataRequest;
import org.springframework.http.ResponseEntity;

public interface AccountDataInterface {
    ResponseEntity<?> processCreateAccountRequest(AccountDataRequest accountDataRequest);

    ResponseEntity<?> fetchAccountDetails(String messageId, String iban, String bicSwift, String cardAlias, Integer pageNumber, Integer pageSize);

    ResponseEntity<?> updateAccountDetails(AccountDataRequest accountDataRequest);

    ResponseEntity<?> deleteAccountDetails(AccountDataRequest accountDataRequest);

}
