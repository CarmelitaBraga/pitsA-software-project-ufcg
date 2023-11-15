package com.ufcg.psoft.commerce.service.entregador;

import com.ufcg.psoft.commerce.dto.entregador.EntregadorPostPutRequestDTO;
import com.ufcg.psoft.commerce.model.entregador.Entregador;

@FunctionalInterface
public interface IEntregadorPostService {
    public Entregador post(EntregadorPostPutRequestDTO entregadorPostPutDTO) throws Exception;
}
