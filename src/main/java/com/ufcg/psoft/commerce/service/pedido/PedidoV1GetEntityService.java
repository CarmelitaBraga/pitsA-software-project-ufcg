package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.exception.estabelecimento.EstabelecimentoNotFound;
import com.ufcg.psoft.commerce.exception.pedido.PedidoEstabelecimentoDoesntMatchException;
import com.ufcg.psoft.commerce.exception.pedido.PedidoNotFoundException;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.repository.EstabelecimentoRepository;
import com.ufcg.psoft.commerce.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoV1GetEntityService implements IPedidoGetEntityService{
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Override
    public Pedido getPedido(Long id) {
        Pedido pedido = pedidoRepository.findPedidoById(id);

        if (pedido == null) {
            throw new PedidoNotFoundException();
        }

        return pedido;
    }

    @Override
    public Pedido validaPedidoEstabelecimento(Long pedidoId, Long estabelecimentoId) {
        Pedido pedido = pedidoRepository.findPedidoById(pedidoId);
        Estabelecimento estabelecimento = estabelecimentoRepository.findEstabelecimentoById(estabelecimentoId);

        if (pedido == null) {
            throw new PedidoNotFoundException();
        }

        if (estabelecimento == null) {
            throw new EstabelecimentoNotFound();
        }

        if (!pedido.getEstabelecimento().getId().equals(estabelecimento.getId())) {
            throw new PedidoEstabelecimentoDoesntMatchException();
        }

        return pedido;
    }


}
