package com.ufcg.psoft.commerce.validation;

import com.ufcg.psoft.commerce.model.sabor.Sabor;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoEntityService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SaborEstabelecimentoValidator implements ConstraintValidator<EstabelecimentoContemSabor, Sabor> {
    private IEstabelecimentoEntityService estabelecimentoGetEntityService;

    @Override
    public void initialize(EstabelecimentoContemSabor constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Sabor sabor, ConstraintValidatorContext context) {
        estabelecimentoGetEntityService.getEstabelecimento(sabor.getEstabelecimento().getId());
        return sabor.getEstabelecimento().getSabores().contains(sabor);
    }
}
