package com.ufcg.psoft.commerce.service.sabor;

import com.ufcg.psoft.commerce.exception.sabor.SaborEstabelecimentoDoesntMatchException;
import com.ufcg.psoft.commerce.exception.sabor.SaborNotFoundException;
import com.ufcg.psoft.commerce.model.sabor.Sabor;
import com.ufcg.psoft.commerce.repository.SaborRepository;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaborGetEntityService implements ISaborGetEntityService {
    @Autowired
    private SaborRepository saborRepository;

    @Autowired
    private IEstabelecimentoEntityService estabelecimentoGetEntityService;

    @Override
    public Sabor verificaSabor(Long saborId, Long estabelecimentoId) {
        Sabor sabor = this.saborRepository.findSaborById(saborId);
        estabelecimentoGetEntityService.getEstabelecimento(estabelecimentoId);

        if(sabor == null)
            throw new SaborNotFoundException();

        if(!sabor.getEstabelecimento().getId().equals(estabelecimentoId))
            throw new SaborEstabelecimentoDoesntMatchException();

        return sabor;
    }
}
