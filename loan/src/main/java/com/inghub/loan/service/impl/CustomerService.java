package com.inghub.loan.service.impl;

import com.inghub.loan.dto.CustomerDto;
import com.inghub.loan.entity.Customer;
import com.inghub.loan.entity.SysUser;
import com.inghub.loan.exception.ResourceNotFoundException;
import com.inghub.loan.mapper.CustomerMapper;
import com.inghub.loan.repository.CustomerRepository;
import com.inghub.loan.repository.SysUserRepository;
import com.inghub.loan.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CustomerService implements ICustomerService {

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private SysUserRepository sysUserRepository;
    @Autowired
    private MessageSource messageSource;

    @Override
    public Long createCustomer(CustomerDto customerDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();
        SysUser user = sysUserRepository.findByUsername(username).orElseThrow(()-> new ResourceNotFoundException("user",
                username, messageSource));
        Customer customer = customerMapper.dto2Model(customerDto);
        customer.setSysUser(user);
        customer.setUsedCreditLimit(BigDecimal.ZERO);
        Customer createdCustomer = customerRepository.save(customer);
        return createdCustomer.getId();
    }

    @Override
    public Customer getCustomer(Long customerId) {
        return customerRepository.findById(customerId).orElseThrow(
                () -> new ResourceNotFoundException("customer", customerId.toString(),
                        messageSource));
    }
}
