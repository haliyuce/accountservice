package com.bank.accountservice.validation;

import org.iban4j.Iban4jException;
import org.iban4j.IbanUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IbanValidator implements ConstraintValidator<IbanValidation, String> {
    @Override
    public boolean isValid(String iban, ConstraintValidatorContext constraintValidatorContext) {
        try {
            IbanUtil.validate(iban);
        } catch (final Iban4jException e) {
            return false;
        }
        return true;
    }
}
