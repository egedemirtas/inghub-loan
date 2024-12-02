package com.inghub.loan.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InstallmentInfoDto {
    private Long id;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private LocalDateTime dueDate;
    private LocalDateTime paymentDate;
    private Boolean isPaid;
}
