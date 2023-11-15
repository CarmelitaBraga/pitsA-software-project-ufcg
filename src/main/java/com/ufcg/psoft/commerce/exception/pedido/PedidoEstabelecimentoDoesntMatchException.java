package com.ufcg.psoft.commerce.exception.pedido;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class PedidoEstabelecimentoDoesntMatchException extends CommerceException {

    public PedidoEstabelecimentoDoesntMatchException() {
        super("Pedido não pertence a este estabelecimento!");
    }
}

