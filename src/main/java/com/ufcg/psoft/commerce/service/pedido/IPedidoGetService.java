package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;

import java.util.List;

public interface IPedidoGetService {
    public List<PedidoResponseDTO> obterPedidosCliente(Long clienteId, String codigoAcesso);

    public PedidoResponseDTO obterPedidoCliente(Long pedidoId, Long clienteId, String codigoAcesso);

    public List<PedidoResponseDTO> buscarPedidosEstabelecimento(Long estabelecimentoId, String codigoAcesso);

    public PedidoResponseDTO buscarPedidoEstabelecimento(Long estabelecimentoId, String codigoAcesso, Long pedidoId);

    public List<PedidoResponseDTO> buscarPedidosClienteFiltradosPorStatus(String statusStr, Long clienteId, String codigoAcesso);
}
