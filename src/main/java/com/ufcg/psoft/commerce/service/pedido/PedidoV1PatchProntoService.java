package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoEntityService;
import com.ufcg.psoft.commerce.service.order.ICommandOrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoV1PatchProntoService implements IPedidoPatchProntoService{


    @Autowired
    IPedidoGetEntityService pedidoGetEntityService;

    @Autowired
    IEstabelecimentoEntityService estabelecimentoEntityService;


    @Autowired
    ICommandOrderService commandOrderService;

    @Autowired
    ModelMapper modelMapper;
    @Override
    public PedidoResponseDTO disparaPronto(Long estabelecimentoId, String codigoAcesso, Long pedidoId) {
        Estabelecimento estabelecimento = estabelecimentoEntityService.verificarLoginEstabelecimento(estabelecimentoId, codigoAcesso);
        Pedido pedido = pedidoGetEntityService.validaPedidoEstabelecimento(pedidoId, estabelecimento.getId());
        pedido.terminoPreparo();
        commandOrderService.adicionaPedidoFila(pedido);


        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }
}
