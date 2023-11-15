package com.ufcg.psoft.commerce.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidVehicleTypeValidator implements ConstraintValidator<ValidVehicleType, String> {
    @Override
    public void initialize(ValidVehicleType constraintAnnotation){

    }

    @Override
    public boolean isValid(String tipo, ConstraintValidatorContext context){
        return  tipo != null && (tipo.toUpperCase().equals("CARRO") ||  tipo.toUpperCase().equals("MOTO"));
    }
}
