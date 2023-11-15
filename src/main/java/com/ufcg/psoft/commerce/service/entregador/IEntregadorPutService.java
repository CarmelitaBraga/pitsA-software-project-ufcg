package com.ufcg.psoft.commerce.service.entregador;

import com.ufcg.psoft.commerce.dto.entregador.EntregadorPostPutRequestDTO;
import com.ufcg.psoft.commerce.model.entregador.Entregador;

@FunctionalInterface
public interface IEntregadorPutService {
    public Entregador put(EntregadorPostPutRequestDTO entregadorPostPutDTO, String codigoAcesso, Long id) throws Exception;
}
