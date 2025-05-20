package com.karenkipchirchir.main.controller;

import com.karenkipchirchir.main.models.request.CustomerBioDataRequest;
import com.karenkipchirchir.main.models.response.CustomerBioDataResponse;
import com.karenkipchirchir.main.service.CustomerDataInterface;
import com.karenkipchirchir.main.utils.RequestValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


@RestController
@RequestMapping("api/v1/customer/")
public class CustomerDataController {

    @Autowired
    RequestValidation requestValidation;

    @Autowired
    CustomerDataInterface customerDataInterface;


    @PostMapping("create")
    public ResponseEntity<?> createCustomerRequest(@RequestBody CustomerBioDataRequest customerBioData){

        CustomerBioDataRequest bioData = requestValidation.validateCustomerRequest(customerBioData,"CREATE");


        if (bioData.getIsValid()){
            return customerDataInterface.processCreateCustomerRequest(customerBioData);
        }else {
            CustomerBioDataResponse customerBioDataResponse = CustomerBioDataResponse
                    .builder()
                    .messageId(customerBioData.getMessageId())
                    .statusCode("1")
                    .statusDescription("Failure")
                    .errorInfoList(bioData.getErrorMessages())
                    .customerId(customerBioData.getCustomerId())
                    .firstName(customerBioData.getFirstName())
                    .lastName(customerBioData.getLastName())
                    .otherName(customerBioData.getOtherName())
                    .build();


            return new ResponseEntity<>(customerBioDataResponse, HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("filter")
    public ResponseEntity<?> fetchCustomerDetails(@RequestParam String messageId,
                                                  @RequestParam String customerNames,
                                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                  @RequestParam Integer pageNumber,
                                                  @RequestParam Integer pageSize){

        return customerDataInterface.processFetchCustomerRequest(messageId,customerNames,startDate,endDate,pageNumber,pageSize);

    }


    @PutMapping("update")
    public ResponseEntity<?> updateCustomerDetails(@RequestBody CustomerBioDataRequest customerBioData){

        CustomerBioDataRequest bioData = requestValidation.validateCustomerRequest(customerBioData, "UPDATE");


        if (bioData.getIsValid()){
            return customerDataInterface.updateCustomerDetails(customerBioData);
        }else {
            CustomerBioDataResponse customerBioDataResponse = CustomerBioDataResponse
                    .builder()
                    .messageId(customerBioData.getMessageId())
                    .statusCode("1")
                    .statusDescription("Failure")
                    .errorInfoList(bioData.getErrorMessages())
                    .customerId(customerBioData.getCustomerId())
                    .firstName(customerBioData.getFirstName())
                    .lastName(customerBioData.getLastName())
                    .otherName(customerBioData.getOtherName())
                    .build();


            return new ResponseEntity<>(customerBioDataResponse, HttpStatus.BAD_REQUEST);
        }
    }



    @DeleteMapping("delete")
    public ResponseEntity<?> deleteCustomerDetails(@RequestBody CustomerBioDataRequest customerBioData){

        CustomerBioDataRequest bioData = requestValidation.validateCustomerRequest(customerBioData, "DELETE");


        if (bioData.getIsValid()){
            return customerDataInterface.deleteCustomerDetails(customerBioData);
        }else {
            CustomerBioDataResponse customerBioDataResponse = CustomerBioDataResponse
                    .builder()
                    .messageId(customerBioData.getMessageId())
                    .statusCode("1")
                    .statusDescription("Failure")
                    .errorInfoList(bioData.getErrorMessages())
                    .customerId(customerBioData.getCustomerId())
                    .firstName(customerBioData.getFirstName())
                    .lastName(customerBioData.getLastName())
                    .otherName(customerBioData.getOtherName())
                    .build();


            return new ResponseEntity<>(customerBioDataResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
