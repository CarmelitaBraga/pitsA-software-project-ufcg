package com.ufcg.psoft.commerce.service.associacao;

import com.ufcg.psoft.commerce.model.associacao.Associacao;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;


public interface IAssociacaoEntityService {
    public Associacao getAssociacao(Estabelecimento estabelecimento, Entregador entregador);
}
