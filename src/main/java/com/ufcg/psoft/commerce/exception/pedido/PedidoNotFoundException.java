package com.ufcg.psoft.commerce.exception.pedido;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class PedidoNotFoundException extends CommerceException {

    public PedidoNotFoundException() {
        super("O pedido consultado nao existe!");
    }
}
