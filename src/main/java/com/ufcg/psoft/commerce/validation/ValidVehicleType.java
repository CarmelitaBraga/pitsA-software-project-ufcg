package com.ufcg.psoft.commerce.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidVehicleTypeValidator.class)
@Documented
public @interface ValidVehicleType {
    String message() default "Tipo do veiculo deve ser moto ou carro";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}