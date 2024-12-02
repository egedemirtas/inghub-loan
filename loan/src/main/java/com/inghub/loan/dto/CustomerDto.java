package com.inghub.loan.dto;

import com.inghub.loan.request.CustomerRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerDto extends CustomerRequest {
    private BigDecimal usedCreditLimit;
}
