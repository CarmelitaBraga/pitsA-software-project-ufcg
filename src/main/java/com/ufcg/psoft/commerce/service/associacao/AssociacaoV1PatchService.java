package com.ufcg.psoft.commerce.service.associacao;

import com.ufcg.psoft.commerce.exception.associacao.AssociacaoEstabelecimentoDoesntMatchException;
import com.ufcg.psoft.commerce.exception.associacao.AssociacaoNotFoundException;
import com.ufcg.psoft.commerce.model.associacao.Associacao;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.repository.AssociacaoRepository;
import com.ufcg.psoft.commerce.service.entregador.IEntregadorEntityService;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssociacaoV1PatchService implements IAssociacaoPatchService {
    @Autowired
    AssociacaoRepository associacaoRepository;

    @Autowired
    IEstabelecimentoEntityService estabelecimentoEntityService;

    @Autowired
    IEntregadorEntityService entregadorEntityService;

    @Override
    public Associacao atualizarStatus(Long associacaoId,
                                      Long estabelecimentoId,
                                      String codigoAcesso,
                                      Boolean status) {

        Estabelecimento estabelecimento = estabelecimentoEntityService.verificarLoginEstabelecimento(estabelecimentoId, codigoAcesso);

        Associacao associacao = associacaoRepository.findAssociacaoById(associacaoId);

        if(associacao == null) {
            throw new AssociacaoNotFoundException();
        }

        if(!associacao.getEstabelecimento().equals(estabelecimento)) {
            throw new AssociacaoEstabelecimentoDoesntMatchException();
        }

        if(!associacao.getStatus() && status){
            Entregador entregador = entregadorEntityService.getEntregador(associacao.getEntregador().getId());
            entregador.setDisponibilidade(false);
            associacao.setEntregador(entregador);
        }


        associacao.setStatus(status);

        associacaoRepository.flush();
        return associacao;
    }
}
