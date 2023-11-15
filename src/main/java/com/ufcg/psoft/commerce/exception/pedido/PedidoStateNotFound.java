package com.ufcg.psoft.commerce.exception.pedido;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class PedidoStateNotFound extends CommerceException {
    public PedidoStateNotFound() {
        super("Status de pedido inválido!");
    }
}
