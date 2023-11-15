package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.exception.cliente.ClienteNotFoundException;
import com.ufcg.psoft.commerce.exception.CodigoInvalidoException;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteV1EntityService implements IClienteEntityService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public Cliente getCliente(Long id) {
        Cliente cliente = this.clienteRepository.findClienteById(id);

        if(cliente == null){
            throw new ClienteNotFoundException();
        }

        return cliente;
    }

    @Override
    public Cliente verificarLoginCliente(Long clienteId, String codigoAcesso) {
        Cliente cliente = clienteRepository.findClienteById(clienteId);

        if (cliente == null) {
            throw new ClienteNotFoundException();
        }

        if (!cliente.getCodigoAcesso().equals(codigoAcesso)) {
            throw new CodigoInvalidoException();
        }

        return cliente;
    }
}
