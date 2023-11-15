package com.ufcg.psoft.commerce.exception.sabor;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class InvalidSaborTypeException extends CommerceException {
    public InvalidSaborTypeException(){
        super("O tipo do Sabor deve ser 'S' ou 'D'");
    }
}
