package com.ufcg.psoft.commerce.service.pedido;


import com.ufcg.psoft.commerce.dto.pedido.PedidoPatchRequestDTO;
import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.commerce.model.pagamento.*;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.repository.PedidoRepository;
import com.ufcg.psoft.commerce.service.cliente.IClienteEntityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;


@Service
public class PedidoV1PatchService implements IPedidoPatchService{
    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    IClienteEntityService clienteGetEntityService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PedidoV1GetEntityService pedidoV1GetEntityService;

    @Override
    public PedidoResponseDTO confirmarPagamento(Long clienteId, String codigoAcesso, Long pedidoId, PedidoPatchRequestDTO pedidoPatchRequestDTO) {
        clienteGetEntityService.verificarLoginCliente(clienteId, codigoAcesso);

        Pedido pedido = pedidoV1GetEntityService.getPedido(pedidoId);

        Character charTipoPagamento = pedidoPatchRequestDTO.getTipoPagamento();
        HashMap<Character, TipoPagamento> tipoPagamento = new HashMap<>();
        tipoPagamento.put('C', CartaoCredito.builder().build());
        tipoPagamento.put('D', CartaoDebito.builder().build());
        tipoPagamento.put('P', Pix.builder().build());

        Pagamento pagamento = Pagamento.builder().tipoPagamento(tipoPagamento.get(charTipoPagamento)).build();
        pagamento.setValorPagamento(pedido.getTotal());
        pagamento.efetuaPagamento();
        pedido.setPagamento(pagamento);
        pedido.confirmaPagamento();

        pedidoRepository.flush();

        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }
}
