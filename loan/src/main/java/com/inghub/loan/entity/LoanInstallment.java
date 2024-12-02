package com.inghub.loan.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class LoanInstallment extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private LocalDateTime dueDate;
    private LocalDateTime paymentDate;
    private Boolean isPaid;

    @ManyToOne
    @JoinColumn(name = "loan", referencedColumnName = "id")
    private Loan loan;
}
