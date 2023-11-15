package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.cliente.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.model.cliente.Cliente;

@FunctionalInterface
public interface IClientePostService {
    public Cliente cadastraCliente(ClientePostPutRequestDTO clientePostPutRequestDTO);
}
