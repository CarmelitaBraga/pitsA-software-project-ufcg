package com.ufcg.psoft.commerce.exception.associacao;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class AssociacaoNotFoundException extends CommerceException {
    public AssociacaoNotFoundException() {
        super("Associacao nao existe!");
    }
}
