package com.ufcg.psoft.commerce.exception.sabor;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class SaborStatusIsAlreadyFalseException extends CommerceException {
    public SaborStatusIsAlreadyFalseException(){
        super("O sabor consultado ja esta indisponivel!");
    }
}
