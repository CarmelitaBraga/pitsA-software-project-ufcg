package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.dto.pedido.PedidoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;

@FunctionalInterface
public interface IPedidoPostService {
    public PedidoResponseDTO cadastraPedido(Long clienteId,
                                            String codigoAcesso,
                                            Long estabelecimentoId,
                                            PedidoPostPutRequestDTO pedidoPostPutRequestDTO);
}
