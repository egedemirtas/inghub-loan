package com.inghub.loan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
public class Customer extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "sysUser", referencedColumnName = "id")
    private SysUser sysUser;

    private BigDecimal creditLimit;
    private BigDecimal usedCreditLimit;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE, orphanRemoval = true,
               fetch = FetchType.EAGER)
    private List<Loan> loans;
}
