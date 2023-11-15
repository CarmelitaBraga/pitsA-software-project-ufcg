package com.ufcg.psoft.commerce.exception.sabor;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class SaborEstabelecimentoDoesntMatchException extends CommerceException {
    public SaborEstabelecimentoDoesntMatchException(){
        super("Sabor não pertence a este estabelecimento!");
    }
}
