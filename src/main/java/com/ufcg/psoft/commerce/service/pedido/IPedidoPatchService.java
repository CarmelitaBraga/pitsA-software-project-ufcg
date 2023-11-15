package com.ufcg.psoft.commerce.service.pedido;


import com.ufcg.psoft.commerce.dto.pedido.PedidoPatchRequestDTO;
import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;


@FunctionalInterface
public interface IPedidoPatchService {


    public PedidoResponseDTO confirmarPagamento(Long clienteId, String codigoAcesso, Long pedidoId, PedidoPatchRequestDTO pedidoPatchRequestDTO);
}
