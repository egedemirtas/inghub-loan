package com.inghub.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanPaymentInfoDto {
    private Integer numberOfInstallmentsPaid;
    private Integer numberOfInstallmentsRemaining;
    private BigDecimal paidAmount;
    private Boolean isLoanPaid;
}
