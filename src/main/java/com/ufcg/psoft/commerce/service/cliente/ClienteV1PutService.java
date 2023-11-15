package com.ufcg.psoft.commerce.service.cliente;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufcg.psoft.commerce.dto.cliente.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ClienteV1PutService implements IClientePutService {

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private IClienteEntityService clienteGetEntityService;

    @Override
    public Cliente atualizaCliente(Long id, String codigoAcesso, ClientePostPutRequestDTO clientePostPutRequestDTO) {
        Cliente cliente = clienteGetEntityService.verificarLoginCliente(id, codigoAcesso);
        Cliente novoCliente = objectMapper.convertValue(clientePostPutRequestDTO, Cliente.class);

        novoCliente.setId(cliente.getId());
        this.clienteRepository.save(novoCliente);
        return this.clienteRepository.findClienteById(novoCliente.getId());
    }
}
