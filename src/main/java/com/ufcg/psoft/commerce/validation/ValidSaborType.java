package com.ufcg.psoft.commerce.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SaborTypeValidator.class)
@Documented
public @interface ValidSaborType {
    String message() default "Tipo deve ser S ou D e não pode ser nulo";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
