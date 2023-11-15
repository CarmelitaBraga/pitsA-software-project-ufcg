package com.ufcg.psoft.commerce.exception.sabor;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class SaborStatusIsAlreadyTrueException extends CommerceException {
    public SaborStatusIsAlreadyTrueException(){
        super("O sabor consultado ja esta disponivel!");
    }
}
