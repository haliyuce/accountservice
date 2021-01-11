package com.bank.accountservice.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IbanValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IbanValidation {
    String message() default "Invalid IBAN number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}