package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.dto.pedido.PedidoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.model.cliente.Endereco;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.pedido.ItemVenda;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.model.pedido.statepedido.StatePedido;
import com.ufcg.psoft.commerce.model.pedido.statepedido.PedidoRecebido;
import com.ufcg.psoft.commerce.repository.PedidoRepository;
import com.ufcg.psoft.commerce.service.cliente.IClienteEntityService;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoEntityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class PedidoV1PostService implements IPedidoPostService {
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private IClienteEntityService clienteGetEntityService;

    @Autowired
    private IEstabelecimentoEntityService estabelecimentoGetEntityService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IPedidoValidadorService pedidoValidadorService;

    @Override
    public PedidoResponseDTO cadastraPedido(Long clienteId, String codigoAcesso, Long estabelecimentoId, PedidoPostPutRequestDTO pedidoPostPutRequestDTO) {
        Cliente cliente = clienteGetEntityService.verificarLoginCliente(clienteId, codigoAcesso);
        Estabelecimento estabelecimento = this.estabelecimentoGetEntityService.getEstabelecimento(estabelecimentoId);
        Endereco endereco = pedidoPostPutRequestDTO.getEndereco();

        if (endereco == null) pedidoPostPutRequestDTO.setEndereco(cliente.getEndereco());

        Collection<ItemVenda> itens = pedidoPostPutRequestDTO.getItens();
        for (ItemVenda item : itens) pedidoValidadorService.verificaSabores(item.getPizza(), estabelecimento);

        Pedido pedido = this.pedidoRepository.save(Pedido.builder()
                .itens(pedidoPostPutRequestDTO.getItens())
                .estabelecimento(estabelecimento)
                .cliente(cliente)
                .build());

        pedido.setEndereco(pedidoPostPutRequestDTO.getEndereco());
        StatePedido pedidoRecebido = PedidoRecebido.builder().build();
        pedidoRecebido.setPedido(pedido);
        pedido.setStatus(pedidoRecebido);

        pedidoRecebido.setOrderNumber(1);

        pedido.getEstabelecimento().getPedidos().add(pedido);
        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }
}
