package com.ufcg.psoft.commerce.service.associacao;

import com.ufcg.psoft.commerce.model.associacao.Associacao;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.repository.AssociacaoRepository;
import com.ufcg.psoft.commerce.service.entregador.IEntregadorLoginService;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssociacaoV1PostService implements IAssociacaoPostService {

    @Autowired
    private AssociacaoRepository associacaoRepository;

    @Autowired
    private IEntregadorLoginService entregadorLoginService;

    @Autowired
    private IEstabelecimentoEntityService estabelecimentoGetEntityService;

    @Override
    public Associacao cadastrar(Long entregadorId, String codigoAcesso, Long estabelecimentoId) {
        Entregador entregador = entregadorLoginService.verificarLogin(entregadorId, codigoAcesso);
        Estabelecimento estabelecimento = estabelecimentoGetEntityService.getEstabelecimento(estabelecimentoId);

        Associacao associacao = Associacao.builder()
                                          .estabelecimento(estabelecimento)
                                          .entregador(entregador)
                                          .status(false)
                                          .build();

        return associacaoRepository.save(associacao);
    }
}