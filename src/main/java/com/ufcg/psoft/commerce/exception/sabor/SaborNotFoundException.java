package com.ufcg.psoft.commerce.exception.sabor;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class SaborNotFoundException extends CommerceException {
    public SaborNotFoundException(){
        super("O sabor consultado nao existe!");
    }
}
