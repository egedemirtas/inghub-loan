package com.inghub.loan.request;

import com.inghub.loan.validator.field.AllowedInstallmentNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "CreateLoanRequest", description = "Contains fields to create a loan")
public class CreateLoanRequest {
    @NotNull(message = "{loan.request.field.error.empty}")
    @Positive(message = "{loan.request.field.error.negative}")
    private Long customerId;

    @NotNull(message = "{loan.request.field.error.empty}")
    @Positive(message = "{loan.request.field.error.negative}")
    private BigDecimal loanAmount;

    @NotNull
    @DecimalMin(value = "0.1", message = "{loan.request.field.error.min}")
    @DecimalMax(value = "0.5", message = "{loan.request.field.error.max}")
    @Digits(integer = 1, fraction = 2)
    private BigDecimal rate;

    @NotNull
    @AllowedInstallmentNumber(message = "{loan.request.field.error.installment}")
    private Integer numberOfInstallment;
}
