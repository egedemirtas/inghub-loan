package com.inghub.loan.controller;

import com.inghub.loan.mapper.CustomerMapper;
import com.inghub.loan.request.CustomerRequest;
import com.inghub.loan.service.ICustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/customer", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class CustomerController {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private CustomerMapper customerMapper;

    @PostMapping
    public ResponseEntity<Long> createCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        Long customerId =
                customerService.createCustomer(customerMapper.customerRequestToCustomerDto(customerRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(customerId);
    }
}
