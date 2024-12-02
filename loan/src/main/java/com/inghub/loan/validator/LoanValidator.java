package com.inghub.loan.validator;

import com.inghub.loan.entity.Customer;
import com.inghub.loan.exception.LimitNotSufficientException;
import com.inghub.loan.repository.CustomerRepository;
import com.inghub.loan.service.impl.CustomerService;
import com.inghub.loan.util.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LoanValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoanValidator.class);

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CustomerService customerService;

    public void validateCustomerId(Long customerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentRole =
                authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst()
                        .orElseThrow(() -> new UsernameNotFoundException("Invalid user"));

        if (currentRole.contains(Role.CUSTOMER.toString())) {
            Customer customer = customerService.getCustomer(customerId);
            String username = (String) authentication.getPrincipal();
            if (!customer.getSysUser().getUsername().equals(username)) {
                throw new UsernameNotFoundException("Invalid user");
            }
        }
    }

    public void validateCustomerLimit(Long customerId, BigDecimal amount) throws LimitNotSufficientException{
        Customer customer = customerService.getCustomer(customerId);
        if (customer.getCreditLimit().subtract(customer.getUsedCreditLimit()).compareTo(amount) < 0){
            throw new LimitNotSufficientException("customer ", "", messageSource);
        }
    }
}
