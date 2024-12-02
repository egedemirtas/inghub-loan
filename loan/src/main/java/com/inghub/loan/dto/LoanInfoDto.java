package com.inghub.loan.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanInfoDto {
    private Long id;
    private BigDecimal loanAmount;
    private Integer numberOfInstallment;
    private Boolean isPaid;
}
