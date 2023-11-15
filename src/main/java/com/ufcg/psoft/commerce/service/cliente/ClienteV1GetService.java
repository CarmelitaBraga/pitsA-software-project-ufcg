package com.ufcg.psoft.commerce.service.cliente;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufcg.psoft.commerce.dto.cliente.ClienteGetResponseDTO;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteV1GetService implements IClienteGetService {

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private IClienteEntityService clienteGetEntityService;

    @Override
    public List<ClienteGetResponseDTO> retornaTodos() {

        List<ClienteGetResponseDTO> clientes = new ArrayList<>();

        for (Cliente cliente : this.clienteRepository.findAll())
            clientes.add(objectMapper.convertValue(cliente, ClienteGetResponseDTO.class));

        return clientes;
    }

    @Override
    public ClienteGetResponseDTO retornaCliente(Long id) {
        Cliente cliente = this.clienteGetEntityService.getCliente(id);

        return objectMapper.convertValue(cliente, ClienteGetResponseDTO.class);
    }
}
