package com.ufcg.psoft.commerce.service.cliente;

@FunctionalInterface
public interface IClienteDeleteService {
    public void deletaCliente(Long id, String codigoAcesso);
}
