package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;

@FunctionalInterface
public interface IPedidoPatchProntoService {

    public PedidoResponseDTO disparaPronto(Long estabelecimentoId, String codigoAcesso, Long pedidoId);
}
