package com.ufcg.psoft.commerce.service.associacao;

import com.ufcg.psoft.commerce.model.associacao.Associacao;

@FunctionalInterface
public interface IAssociacaoPatchService {
    public Associacao atualizarStatus(Long associacaoId,
                                      Long estabelecimentoId,
                                      String codigoAcesso,
                                      Boolean status);
}
