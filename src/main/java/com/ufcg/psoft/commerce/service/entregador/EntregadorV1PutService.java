package com.ufcg.psoft.commerce.service.entregador;

import com.ufcg.psoft.commerce.dto.entregador.EntregadorPostPutRequestDTO;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.repository.EntregadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntregadorV1PutService implements IEntregadorPutService {
    @Autowired
    private EntregadorRepository entregadorRepository;
    @Autowired
    private EntregadorV1LoginService entregadorLoginService;

    public Entregador put(EntregadorPostPutRequestDTO entregadorPostPutDTO, String codigoAcesso, Long id) throws Exception {
        Entregador entregador = entregadorLoginService.verificarLogin(id, codigoAcesso);
        entregador.setNome(entregadorPostPutDTO.getNome());
        entregador.setVeiculo(entregadorPostPutDTO.getVeiculo());
        entregador.setCodigoAcesso(entregadorPostPutDTO.getCodigoAcesso());
        this.entregadorRepository.save(entregador);
        return this.entregadorRepository.findEntregadorById(id);
    }
}
