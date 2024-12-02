package com.inghub.loan.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanPaymentDto {
    private Long customerId;
    private Long loanId;
    private BigDecimal amount;
}
