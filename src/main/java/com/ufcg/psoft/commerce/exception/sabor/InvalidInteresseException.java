package com.ufcg.psoft.commerce.exception.sabor;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class InvalidInteresseException extends CommerceException {
    public InvalidInteresseException(){
        super("O sabor consultado ja esta disponivel!");
    }
}
