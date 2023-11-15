package com.ufcg.psoft.commerce.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
public class SixDigitPasswordValidator implements ConstraintValidator<ValidSixDigitPassword, String> {
    @Override
    public void initialize(ValidSixDigitPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Verifica se a senha possui exatamente 6 dígitos
        return value != null && value.matches("^\\d{6}$");
    }

}
