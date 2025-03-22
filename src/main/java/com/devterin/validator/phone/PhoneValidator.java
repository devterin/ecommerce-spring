package com.devterin.validator.phone;

import com.devterin.exception.ErrorCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

    @Override
    public void initialize(Phone constraintAnnotation) {
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        String[] validPatterns = {
                "\\d{10}",
                "\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}" //validating phone number with -, . or spaces
        };
        if (phoneNumber == null) {
            context.disableDefaultConstraintViolation(); // vô hiệu tb lỗi mặc định (nếu có)
            context.buildConstraintViolationWithTemplate(ErrorCode.PHONE_INVALID.getMessage())
                    .addConstraintViolation(); // lấy message từ enum ErrorCode để hiển thị.
            return false;
        }

        for (String pattern : validPatterns) {
            if (phoneNumber.matches(pattern)) {
                return true;
            }
        }
        return false;
    }
}
