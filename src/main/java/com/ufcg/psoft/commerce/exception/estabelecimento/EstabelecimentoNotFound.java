package com.ufcg.psoft.commerce.exception.estabelecimento;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class EstabelecimentoNotFound extends CommerceException {

    public EstabelecimentoNotFound(){
        super("O estabelecimento nao existe!");
    }
}
