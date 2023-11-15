package com.ufcg.psoft.commerce.service.associacao;

import com.ufcg.psoft.commerce.model.associacao.Associacao;

@FunctionalInterface
public interface IAssociacaoPostService {
    public Associacao cadastrar(Long entregadorId, String codigoAcesso, Long estabelecimentoId);
}
