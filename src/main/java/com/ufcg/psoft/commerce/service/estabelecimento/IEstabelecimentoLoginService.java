package com.ufcg.psoft.commerce.service.estabelecimento;

import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;

@FunctionalInterface
public interface IEstabelecimentoLoginService {

    public Estabelecimento verificarLogin(Long estabelecimentoId, String codigoAcesso);
}
