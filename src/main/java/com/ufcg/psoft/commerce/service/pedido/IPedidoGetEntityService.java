package com.ufcg.psoft.commerce.service.pedido;
import com.ufcg.psoft.commerce.model.pedido.Pedido;

public interface IPedidoGetEntityService {
    public Pedido getPedido(Long id);
    public Pedido validaPedidoEstabelecimento(Long pedidoId, Long estabelecimentoId);

}
