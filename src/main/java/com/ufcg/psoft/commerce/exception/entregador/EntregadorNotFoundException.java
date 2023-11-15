package com.ufcg.psoft.commerce.exception.entregador;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class EntregadorNotFoundException extends CommerceException {
    public EntregadorNotFoundException(){
        super("O entregador consultado nao existe!");
    }
}
