package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.exception.pedido.PedidoClienteDoesntMatchException;
import com.ufcg.psoft.commerce.exception.pedido.PedidoEstabelecimentoDoesntMatchException;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.repository.PedidoRepository;
import com.ufcg.psoft.commerce.service.cliente.IClienteEntityService;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoV1DeleteService implements IPedidoDeleteService {

    @Autowired
    IClienteEntityService clienteEntityService;

    @Autowired
    IEstabelecimentoEntityService estabelecimentoEntityService;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    PedidoV1GetEntityService pedidoV1GetEntityService;

    @Override
    public void cancelarPedidoCliente(Long id, Long clienteId, String codigoAcesso) {
        Cliente cliente = clienteEntityService.verificarLoginCliente(clienteId, codigoAcesso);
        Pedido pedido = pedidoV1GetEntityService.getPedido(id);

        if (!cliente.equals(pedido.getCliente())) throw new PedidoClienteDoesntMatchException();

        pedido.cancelaPedido();
        
        pedidoRepository.deleteById(id);
    }

    @Override
    public void apagarPedidoEstabelecimento(Long id, Long estabelecimentoId, String codigoAcesso) {
        Estabelecimento estabelecimento = estabelecimentoEntityService.verificarLoginEstabelecimento(estabelecimentoId, codigoAcesso);
        Pedido pedido = pedidoV1GetEntityService.getPedido(id);

        if (!estabelecimento.equals(pedido.getEstabelecimento())) throw new PedidoEstabelecimentoDoesntMatchException();

        pedidoRepository.deleteById(id);
    }
}
