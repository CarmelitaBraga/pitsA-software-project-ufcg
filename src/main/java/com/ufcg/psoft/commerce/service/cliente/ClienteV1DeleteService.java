package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteV1DeleteService implements IClienteDeleteService {
    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    private IClienteEntityService clienteGetEntityService;

    @Override
    public void deletaCliente(Long id, String codigoAcesso) {
        clienteGetEntityService.verificarLoginCliente(id, codigoAcesso);
        this.clienteRepository.deleteById(id);
    }
}
