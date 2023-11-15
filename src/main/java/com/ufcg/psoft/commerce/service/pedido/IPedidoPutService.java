package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.dto.pedido.PedidoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;

@FunctionalInterface
public interface IPedidoPutService {
    public PedidoResponseDTO atualizaPedido(Long pedidoId, Long clienteId, String codigoAcesso, PedidoPostPutRequestDTO pedidoPostPutRequestDTO);
}
