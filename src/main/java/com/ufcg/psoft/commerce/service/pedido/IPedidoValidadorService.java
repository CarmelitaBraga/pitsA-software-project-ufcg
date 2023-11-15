package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.pizza.Pizza;

@FunctionalInterface
public interface IPedidoValidadorService {
    public void verificaSabores(Pizza pizza, Estabelecimento estabelecimento);
}
