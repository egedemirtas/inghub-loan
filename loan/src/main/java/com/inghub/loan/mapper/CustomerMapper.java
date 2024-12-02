package com.inghub.loan.mapper;

import com.inghub.loan.dto.CustomerDto;
import com.inghub.loan.entity.Customer;
import com.inghub.loan.request.CustomerRequest;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface CustomerMapper {
    CustomerDto customerRequestToCustomerDto(CustomerRequest customerRequest);

    Customer dto2Model(CustomerDto customerDto);
}
