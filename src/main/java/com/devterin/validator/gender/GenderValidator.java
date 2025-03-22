package com.devterin.validator.gender;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Stream;

public class GenderValidator implements ConstraintValidator<GenderSubSet, CharSequence> {
    private List<String> genders;
    @Override
    public void initialize(GenderSubSet constraint) {
        genders = Stream.of(constraint.enumClass().getEnumConstants())
                .map(Enum::name)
                .toList();
    }

    @Override
    public boolean isValid(CharSequence genderType, ConstraintValidatorContext constraintValidatorContext) {
        if (genderType == null) return true;
        return genders.contains(genderType.toString().toUpperCase());
    }
}
