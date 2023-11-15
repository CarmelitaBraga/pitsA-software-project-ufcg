package com.ufcg.psoft.commerce.service.cliente;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufcg.psoft.commerce.dto.cliente.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteV1PostService implements IClientePostService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public Cliente cadastraCliente(ClientePostPutRequestDTO clientePostPutRequestDTO) {
        return this.clienteRepository.save(
                objectMapper.convertValue(clientePostPutRequestDTO, Cliente.class));
    }
}
