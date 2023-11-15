package com.ufcg.psoft.commerce.service.sabor;

import com.ufcg.psoft.commerce.model.sabor.Sabor;

@FunctionalInterface
public interface ISaborGetEntityService {
    public Sabor verificaSabor(Long saborId, Long estabelecimentoId);
}
