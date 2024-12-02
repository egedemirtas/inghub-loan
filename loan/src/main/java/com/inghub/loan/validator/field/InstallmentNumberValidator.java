package com.inghub.loan.validator.field;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static com.inghub.loan.util.Constant.ALLOWED_INSTALLMENT_LIST;

public class InstallmentNumberValidator implements ConstraintValidator<AllowedInstallmentNumber, Integer> {


    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return ALLOWED_INSTALLMENT_LIST.contains(value);
    }
}
