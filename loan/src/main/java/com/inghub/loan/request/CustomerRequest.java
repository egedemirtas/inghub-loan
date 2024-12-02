package com.inghub.loan.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerRequest {
    @NotBlank(message = "{loan.request.field.error.empty}")
    private String name;
    @NotBlank(message = "{loan.request.field.error.empty}")
    private String surname;
    @NotNull(message = "{loan.request.field.error.empty}")
    @Positive(message = "{loan.request.field.error.negative}")
    private BigDecimal creditLimit;
}
