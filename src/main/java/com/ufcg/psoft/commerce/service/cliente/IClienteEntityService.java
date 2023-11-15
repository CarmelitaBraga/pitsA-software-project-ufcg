package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.model.cliente.Cliente;

public interface IClienteEntityService {
    public Cliente getCliente(Long id);

    public Cliente verificarLoginCliente(Long id, String codigoAcesso);
}
