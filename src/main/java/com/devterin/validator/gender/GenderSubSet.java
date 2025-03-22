package com.devterin.validator.gender;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = GenderValidator.class)
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
public @interface GenderSubSet {
    String name();
    String message() default "GENDER_INVALID";
    Class<? extends Enum<?>> enumClass();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
