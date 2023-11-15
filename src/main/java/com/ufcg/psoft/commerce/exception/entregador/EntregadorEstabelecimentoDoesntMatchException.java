package com.ufcg.psoft.commerce.exception.entregador;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class EntregadorEstabelecimentoDoesntMatchException  extends CommerceException {
    public EntregadorEstabelecimentoDoesntMatchException(){
        super("O entregador não pertence a este estabelecimento!");
    }
}