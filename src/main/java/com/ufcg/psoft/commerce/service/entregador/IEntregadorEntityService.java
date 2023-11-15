package com.ufcg.psoft.commerce.service.entregador;

import com.ufcg.psoft.commerce.model.entregador.Entregador;

public interface IEntregadorEntityService {
    public Entregador getEntregador(Long id);
    public Entregador validarEntregadorEstabelecimento(Long idEntregador, Long idEstabelecimento);
}
