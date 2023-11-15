package com.ufcg.psoft.commerce.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SixDigitPasswordValidator.class)
@Documented
public @interface ValidSixDigitPassword {
    String message() default "Codigo de acesso deve ter exatamente 6 digitos numericos";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
