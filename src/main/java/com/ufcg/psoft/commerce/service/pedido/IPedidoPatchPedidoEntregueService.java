package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;
@FunctionalInterface
public interface IPedidoPatchPedidoEntregueService {
    public PedidoResponseDTO clienteRecebePedido(Long clienteId, String codigoAcesso, Long pedidoId);

}
