package com.ufcg.psoft.commerce.exception.pedido;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class PedidoClienteDoesntMatchException extends CommerceException {
    public PedidoClienteDoesntMatchException() {
        super("Cliente não pertence a esse pedido!");
    }
}
