package com.ufcg.psoft.commerce.service.order;


import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.model.pedido.Pedido;

public interface ICommandOrderService {

    public void adicionaEntregadorFila(Entregador entregador);

    public void adicionaPedidoFila(Pedido pedido);

    public void removeEntregadorFila(Entregador entregador);

}
