package com.devterin.validator.phone;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface Phone {
    String message() default "PHONE_INVALID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}