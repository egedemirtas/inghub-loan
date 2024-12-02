package com.inghub.loan.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayLoanRequest {
    private Long customerId;
    private Long loanId;
    private BigDecimal amount;
}
