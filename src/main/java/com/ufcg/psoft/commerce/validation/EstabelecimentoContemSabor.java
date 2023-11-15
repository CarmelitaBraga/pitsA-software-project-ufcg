package com.ufcg.psoft.commerce.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SaborEstabelecimentoValidator.class)
@Documented
public @interface EstabelecimentoContemSabor {
    String message() default "Sabor deve pertencer a estabelecimento!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
