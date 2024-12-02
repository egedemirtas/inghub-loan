package com.inghub.loan.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanDto {
    private Long customerId;
    private BigDecimal loanAmount;
    private BigDecimal rate;
    private Integer numberOfInstallment;
}
