package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.exception.sabor.SaborEstabelecimentoDoesntMatchException;
import com.ufcg.psoft.commerce.model.pizza.Pizza;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.sabor.Sabor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class PedidoV1ValidadorService implements IPedidoValidadorService {
    @Override
    public void verificaSabores(Pizza pizza, Estabelecimento estabelecimento) {
        Collection<Sabor> sabores = pizza.acessaSabores();
        if (sabores.stream().anyMatch(sabor ->
                !sabor.getEstabelecimento().getId().equals(estabelecimento.getId())))
            throw new SaborEstabelecimentoDoesntMatchException();
    }
}