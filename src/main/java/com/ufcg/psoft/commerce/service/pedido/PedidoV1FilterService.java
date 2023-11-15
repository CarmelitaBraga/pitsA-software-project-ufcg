package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.exception.pedido.PedidoStateNotFound;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.model.pedido.statepedido.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PedidoV1FilterService implements IPedidoFilterService {
    private Map<Integer, StatePedido> states;

    public PedidoV1FilterService() {
        states = new HashMap<>();

        StatePedido recebido = PedidoRecebido.builder().build();
        StatePedido preparo = PedidoEmPreparo.builder().build();
        StatePedido pronto = PedidoPronto.builder().build();
        StatePedido emRota = PedidoEmRota.builder().build();
        StatePedido entregue = PedidoEntregue.builder().build();

        states.put(1, recebido);
        states.put(2, preparo);
        states.put(3, pronto);
        states.put(4, emRota);
        states.put(5, entregue);
    }

    @Override
    public List<Pedido> filterPedidosByState(List<Pedido> pedidos, Integer statePedidoNumber) {
        return pedidos.stream()
                .filter(pedido -> pedido.getStatus().getOrderNumber().equals(statePedidoNumber))
                .collect(Collectors.toList());
    }

    @Override
    public List<Pedido> filterAndOrderAllPedidos(List<Pedido> pedidos) {
        List<Pedido> orderedList = new ArrayList<>();
        for (int state = 1; state <= 5; state++) {
            orderedList.addAll(filterPedidosByState(pedidos, state));
        }

        return orderedList;
    }

    @Override
    public Integer verificaStatus(String statusStr) {
        Integer status;

        if (statusStr.toUpperCase().contains("RECEBIDO")) status = 1;
        else if (statusStr.toUpperCase().contains("PREPARO")) status = 2;
        else if (statusStr.toUpperCase().contains("PRONTO")) status = 3;
        else if (statusStr.toUpperCase().contains("ROTA")) status = 4;
        else if (statusStr.toUpperCase().contains("ENTREGUE")) status = 5;
        else throw new PedidoStateNotFound();

        return status;
    }

    @Override
    public StatePedido retornaState(Integer statusNumber) {
        return this.states.get(statusNumber);
    }

}
