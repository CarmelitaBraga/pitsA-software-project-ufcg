package com.ufcg.psoft.commerce.service.entregador;

import com.ufcg.psoft.commerce.repository.EntregadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntregadorV1DeleteService implements IEntregadorDeleteService {
    @Autowired
    private EntregadorRepository entregadorRepository;
    @Autowired
    private EntregadorV1LoginService entregadorLoginService;
    public void delete(Long id, String codigoAcesso) throws Exception{
        entregadorLoginService.verificarLogin(id, codigoAcesso);
        this.entregadorRepository.deleteById(id);
    }

}
