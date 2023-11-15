package com.ufcg.psoft.commerce.exception.associacao;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class AssociacaoEntregadorNotFoundException extends CommerceException {

    public AssociacaoEntregadorNotFoundException() {
        super("O Entregador não está aprovado em nenhum estabelecimento!");
    }

}
