package com.ufcg.psoft.commerce.service.entregador;

import com.ufcg.psoft.commerce.exception.entregador.EntregadorNotFoundException;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.repository.EntregadorRepository;
import com.ufcg.psoft.commerce.service.associacao.IAssociacaoEntityService;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntregadorV1EntityService implements IEntregadorEntityService{
    @Autowired
    private EntregadorRepository entregadorRepository;

    @Autowired
    private IEstabelecimentoEntityService estabelecimentoEntityService;

    @Autowired
    private IAssociacaoEntityService associacaoEntityService;

    @Override
    public Entregador getEntregador(Long id) {
        Entregador entregador = entregadorRepository.findEntregadorById(id);

        if(entregador == null){
            throw new EntregadorNotFoundException();
        }
        return entregador;
    }

    @Override
    public Entregador validarEntregadorEstabelecimento(Long idEntregador, Long idEstabelecimento) {
        Entregador entregador = this.getEntregador(idEntregador);
        Estabelecimento estabelecimento = estabelecimentoEntityService.getEstabelecimento(idEstabelecimento);

        associacaoEntityService.getAssociacao(estabelecimento, entregador);

        return entregador;
    }
}
