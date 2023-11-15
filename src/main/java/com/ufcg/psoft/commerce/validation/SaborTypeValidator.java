package com.ufcg.psoft.commerce.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SaborTypeValidator implements ConstraintValidator<ValidSaborType, Character> {
    @Override
    public void initialize(ValidSaborType constraintAnnotation) {
    }

    @Override
    public boolean isValid(Character pizzaTipo, ConstraintValidatorContext context) {
        Character salgada = Character.valueOf('S');
        Character doce = Character.valueOf('D');

        return pizzaTipo != null && (salgada.equals(Character.toUpperCase(pizzaTipo)) || doce.equals(Character.toUpperCase(pizzaTipo)));
    }
}
