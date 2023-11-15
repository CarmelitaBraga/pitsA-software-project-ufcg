package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;

@FunctionalInterface
public interface IPedidoPatchAtribuirEntregadorService {
    public PedidoResponseDTO atribuirEntregador(Long idEstabelecimento, String codigoAcesso, Long idPedido, Long idEntregador);
}
