package com.inghub.loan.service;

import com.inghub.loan.dto.CustomerDto;
import com.inghub.loan.entity.Customer;

public interface ICustomerService {

    Long createCustomer(CustomerDto customerDto);

    Customer getCustomer(Long customerId);
}
