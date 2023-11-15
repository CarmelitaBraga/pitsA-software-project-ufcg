package com.ufcg.psoft.commerce.service.entregador;

import com.ufcg.psoft.commerce.model.entregador.Entregador;

@FunctionalInterface
public interface IEntregadorLoginService {

    public Entregador verificarLogin(Long entregadorId, String codigoAcesso);
}
