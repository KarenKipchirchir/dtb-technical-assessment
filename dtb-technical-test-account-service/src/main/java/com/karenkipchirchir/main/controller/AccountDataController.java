package com.karenkipchirchir.main.controller;

import com.karenkipchirchir.main.models.request.AccountDataRequest;
import com.karenkipchirchir.main.models.response.AccountDataResponse;
import com.karenkipchirchir.main.service.AccountDataInterface;
import com.karenkipchirchir.main.utils.RequestValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("api/v1/account/")
public class AccountDataController {

    @Autowired
    RequestValidation requestValidation;

    @Autowired
    AccountDataInterface accountDataInterface;


    @PostMapping("create")
    public ResponseEntity<?> createAccountRequest(@RequestBody AccountDataRequest accountDataRequest){

        AccountDataRequest request = requestValidation.validateAccountRequest(accountDataRequest,"CREATE");


        if (request.getIsValid()){
            return accountDataInterface.processCreateAccountRequest(accountDataRequest);
        }else {
            AccountDataResponse accountDataRequestResponse = AccountDataResponse
                    .builder()
                    .messageId(accountDataRequest.getMessageId())
                    .statusCode("1")
                    .statusDescription("Failure")
                    .errorInfoList(request.getErrorMessages())
                    .bicSwift(accountDataRequest.getBicSwift())
                    .build();


            return new ResponseEntity<>(accountDataRequestResponse, HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("filter")
    public ResponseEntity<?> fetchAccountDetails(@RequestParam String messageId,
                                                  @RequestParam String iban,
                                                  @RequestParam String bicSwift,
                                                  @RequestParam String cardAlias,
                                                  @RequestParam Integer pageNumber,
                                                  @RequestParam Integer pageSize){

        return accountDataInterface.fetchAccountDetails(messageId,iban,bicSwift,cardAlias,pageNumber,pageSize);

    }

    @PutMapping("update")
    public ResponseEntity<?> updateAccountDetails(@RequestBody AccountDataRequest accountDataRequest){

        AccountDataRequest request = requestValidation.validateAccountRequest(accountDataRequest, "UPDATE");


        if (request.getIsValid()){
            return accountDataInterface.updateAccountDetails(accountDataRequest);
        }else {
            AccountDataResponse accountDataRequestResponse = AccountDataResponse
                    .builder()
                    .messageId(accountDataRequest.getMessageId())
                    .statusCode("1")
                    .statusDescription("Failure")
                    .errorInfoList(request.getErrorMessages())
                    .accountId(accountDataRequest.getAccountId())
                    .bicSwift(accountDataRequest.getBicSwift())
                    .build();


            return new ResponseEntity<>(accountDataRequestResponse, HttpStatus.BAD_REQUEST);
        }
    }



    @DeleteMapping("delete")
    public ResponseEntity<?> deleteAccountDetails(@RequestBody AccountDataRequest accountDataRequest){

        AccountDataRequest request = requestValidation.validateAccountRequest(accountDataRequest, "DELETE");


        if (request.getIsValid()){
            return accountDataInterface.deleteAccountDetails(accountDataRequest);
        }else {
            AccountDataResponse accountDataRequestResponse = AccountDataResponse
                    .builder()
                    .messageId(accountDataRequest.getMessageId())
                    .statusCode("1")
                    .statusDescription("Failure")
                    .errorInfoList(request.getErrorMessages())
                    .accountId(accountDataRequest.getAccountId())
                    .build();


            return new ResponseEntity<>(accountDataRequestResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
