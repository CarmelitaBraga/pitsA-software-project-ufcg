package com.ufcg.psoft.commerce.exception.entregador;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class EntregadorIsUnavailableException extends CommerceException {
    public EntregadorIsUnavailableException(){
        super("Entregador está indisponível para entrega!");
    }
}
