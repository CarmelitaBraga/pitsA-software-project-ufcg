package com.ufcg.psoft.commerce.exception.pedido;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class IllegalStateChangeException extends CommerceException {

    public IllegalStateChangeException(){
        super("Mudança de estado inválida!");
    }
}
