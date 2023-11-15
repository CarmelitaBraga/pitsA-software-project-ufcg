package com.ufcg.psoft.commerce.service.associacao;

import com.ufcg.psoft.commerce.exception.associacao.AssociacaoNotFoundException;
import com.ufcg.psoft.commerce.exception.entregador.EntregadorEstabelecimentoDoesntMatchException;
import com.ufcg.psoft.commerce.model.associacao.Associacao;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.repository.AssociacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssociacaoV1EntityService implements IAssociacaoEntityService {
    @Autowired
    private AssociacaoRepository associacaoRepository;

    @Override
    public Associacao getAssociacao(Estabelecimento estabelecimento, Entregador entregador) {
        Associacao associacao = associacaoRepository.findAssociacaoByEstabelecimentoAndEntregador(estabelecimento, entregador);

        if (associacao == null) throw new EntregadorEstabelecimentoDoesntMatchException();

        return associacao;
    }
}
