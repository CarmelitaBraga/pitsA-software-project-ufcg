package com.ufcg.psoft.commerce.exception.cliente;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class ClienteNotFoundException extends CommerceException {

    public ClienteNotFoundException() { super("Cliente não existe!"); }
}
