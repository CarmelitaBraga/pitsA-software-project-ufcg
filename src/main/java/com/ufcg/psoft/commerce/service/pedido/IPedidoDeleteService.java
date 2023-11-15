package com.ufcg.psoft.commerce.service.pedido;

public interface IPedidoDeleteService {
    public void cancelarPedidoCliente(Long id, Long clienteId, String codigoAcesso);

    public void apagarPedidoEstabelecimento(Long id, Long estabelecimentoId, String codigoAcesso);
}
