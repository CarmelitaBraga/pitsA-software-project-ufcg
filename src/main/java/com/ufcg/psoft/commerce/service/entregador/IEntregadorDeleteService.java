package com.ufcg.psoft.commerce.service.entregador;

@FunctionalInterface
public interface IEntregadorDeleteService {
    public void delete(Long id, String codigoAcesso)  throws Exception;
}
