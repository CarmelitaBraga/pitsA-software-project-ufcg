package com.ufcg.psoft.commerce.service.entregador;

import com.ufcg.psoft.commerce.exception.CodigoInvalidoException;
import com.ufcg.psoft.commerce.exception.entregador.EntregadorNotFoundException;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.repository.EntregadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntregadorV1LoginService implements IEntregadorLoginService {
    @Autowired
    private EntregadorRepository entregadorRepository;

    @Override
    public Entregador verificarLogin(Long entregadorId, String codigoAcesso) {
        Entregador entregador = entregadorRepository.findEntregadorById(entregadorId);

        if (entregador == null) {
            throw new EntregadorNotFoundException();
        }

        if(!entregador.getCodigoAcesso().equals(codigoAcesso)){
            throw new CodigoInvalidoException();
        }

        return entregador;
    }
}
