package com.ufcg.psoft.commerce.exception;

public class CodigoInvalidoException extends CommerceException {
    public CodigoInvalidoException(){
        super("Código de acesso inválido!");
    }
}