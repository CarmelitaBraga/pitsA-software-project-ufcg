package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.dto.pedido.PedidoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.commerce.model.cliente.Endereco;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.repository.PedidoRepository;
import com.ufcg.psoft.commerce.service.cliente.IClienteEntityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoV1PutService implements IPedidoPutService {
    @Autowired
    PedidoRepository pedidoRepository;
    @Autowired
    IClienteEntityService clienteGetEntityService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    IPedidoGetEntityService iPedidoGetEntityService;

    @Override
    public PedidoResponseDTO atualizaPedido(Long pedidoId, Long clienteId, String codigoAcesso, PedidoPostPutRequestDTO pedidoPostPutRequestDTO) {
        clienteGetEntityService.verificarLoginCliente(clienteId, codigoAcesso);
        Pedido pedido = iPedidoGetEntityService.getPedido(pedidoId);

        Endereco endereco = pedidoPostPutRequestDTO.getEndereco();

        pedidoRepository.flush();

        if (endereco != null) {
            pedido.setEndereco(endereco);
        }

        if (pedidoPostPutRequestDTO.getItens() != null
                && !pedidoPostPutRequestDTO.getItens().isEmpty())
            pedido.setItens(pedidoPostPutRequestDTO.getItens());

        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }
}
