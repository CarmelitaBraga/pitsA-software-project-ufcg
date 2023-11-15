package com.ufcg.psoft.commerce.service.entregador;

import com.ufcg.psoft.commerce.dto.entregador.EntregadorPostPutRequestDTO;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.repository.EntregadorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntregadorV1PostService implements IEntregadorPostService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EntregadorRepository entregadorRepository;
    public Entregador post(EntregadorPostPutRequestDTO entregadorPostPutDTO) throws Exception{
        Entregador entregador = modelMapper.map(entregadorPostPutDTO, Entregador.class);
        Entregador entregadorSalvo = this.entregadorRepository.save(entregador);
        return this.entregadorRepository.findEntregadorById(entregadorSalvo.getId());

    }
}
