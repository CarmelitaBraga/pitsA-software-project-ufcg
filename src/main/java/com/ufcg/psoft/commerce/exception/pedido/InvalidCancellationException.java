package com.ufcg.psoft.commerce.exception.pedido;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class InvalidCancellationException extends CommerceException {

    public InvalidCancellationException(){
        super("O pedido já está pronto, não pode ser cancelado!");
    }
}
