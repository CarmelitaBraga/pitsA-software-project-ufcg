package com.ufcg.psoft.commerce.exception.associacao;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class AssociacaoEstabelecimentoDoesntMatchException extends CommerceException {
    public AssociacaoEstabelecimentoDoesntMatchException() {
        super("Associacao não pertence a esse estabelecimento!");
    }
}
