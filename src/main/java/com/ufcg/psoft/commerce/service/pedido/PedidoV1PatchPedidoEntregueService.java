package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.commerce.exception.pedido.PedidoClienteDoesntMatchException;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.service.Notificacao.NotificacaoEnviarService;
import com.ufcg.psoft.commerce.service.cliente.ClienteV1EntityService;
import com.ufcg.psoft.commerce.service.order.CommandOrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoV1PatchPedidoEntregueService implements IPedidoPatchPedidoEntregueService {
    @Autowired
    ClienteV1EntityService clienteV1GetEntityService;

    @Autowired
    PedidoV1GetEntityService pedidoV1GetEntityService;

    @Autowired
    NotificacaoEnviarService notificacaoEnviarService;

    @Autowired
    CommandOrderService commandOrderService;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public PedidoResponseDTO clienteRecebePedido(Long clienteId, String codigoAcesso, Long pedidoId) {
        Cliente cliente = clienteV1GetEntityService.verificarLoginCliente(clienteId, codigoAcesso);
        Pedido pedido = this.pedidoV1GetEntityService.getPedido(pedidoId);
        if(!pedido.getCliente().equals(cliente)) throw new PedidoClienteDoesntMatchException();
        pedido.clienteConfirmaEntrega();
        commandOrderService.adicionaEntregadorFila(pedido.getEntregador());

        this.notificaEstabelecimento(pedido, cliente);

        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }

    private void notificaEstabelecimento(Pedido pedido, Cliente cliente) {
        Estabelecimento estabelecimento = pedido.getEstabelecimento();
        String assunto = "O pedido #" + pedido.getId() + " foi entregue!";
        String texto = "O cliente "+ cliente.getNome() + " recebeu o pedido #"+ pedido.getId() + " com sucesso!";

        this.notificacaoEnviarService.enviarEmail(estabelecimento.getEmail(), assunto, texto);
    }
}
