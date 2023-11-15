package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.commerce.exception.pedido.PedidoClienteDoesntMatchException;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.repository.PedidoRepository;
import com.ufcg.psoft.commerce.service.cliente.IClienteEntityService;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoEntityService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class PedidoV1GetService implements IPedidoGetService {
    @Autowired
    private IClienteEntityService clienteGetEntityService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    IPedidoGetEntityService pedidoGetEntityService;

    @Autowired
    IEstabelecimentoEntityService estabelecimentoEntityService;

    @Autowired
    IPedidoFilterService pedidoFilterService;

    @Override
    public List<PedidoResponseDTO> obterPedidosCliente(Long clienteId, String codigoAcesso) {
        Cliente cliente = clienteGetEntityService.verificarLoginCliente(clienteId, codigoAcesso);
        List<Pedido> pedidos = pedidoRepository.findPedidosByClienteOrderByTimestampDesc(cliente);
        List<Pedido> pedidosCliente = pedidoFilterService.filterAndOrderAllPedidos(pedidos);

        return modelMapper.map(pedidosCliente, new TypeToken<List<PedidoResponseDTO>>() {}.getType());
    }

    @Override
    public PedidoResponseDTO obterPedidoCliente(Long pedidoId, Long clienteId, String codigoAcesso) {
        Cliente cliente = clienteGetEntityService.verificarLoginCliente(clienteId, codigoAcesso);
        Pedido pedido = pedidoGetEntityService.getPedido(pedidoId);

        if (!pedido.getCliente().equals(cliente)) throw new PedidoClienteDoesntMatchException();

        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }

    @Override
    public List<PedidoResponseDTO> buscarPedidosEstabelecimento(Long estabelecimentoId, String codigoAcesso) {
        Estabelecimento estabelecimento = estabelecimentoEntityService.verificarLoginEstabelecimento(estabelecimentoId, codigoAcesso);

        List<Pedido> pedidos = pedidoRepository.findPedidosByEstabelecimento(estabelecimento);

        List<PedidoResponseDTO> pedidosResponseDTOs = new ArrayList<>();
        for (Pedido pedido : pedidos)
            pedidosResponseDTOs.add(modelMapper.map(pedido, PedidoResponseDTO.class));

        return pedidosResponseDTOs;
    }

    @Override
    public PedidoResponseDTO buscarPedidoEstabelecimento(Long estabelecimentoId, String codigoAcesso, Long pedidoId) {
        Estabelecimento estabelecimento = estabelecimentoEntityService.verificarLoginEstabelecimento(estabelecimentoId, codigoAcesso);
        Pedido pedido = pedidoGetEntityService.validaPedidoEstabelecimento(pedidoId, estabelecimento.getId());

        return modelMapper.map(pedido, PedidoResponseDTO.class);

    }

    @Override
    public List<PedidoResponseDTO> buscarPedidosClienteFiltradosPorStatus(String statusStr, Long clienteId, String codigoAcesso) {
        Integer status = pedidoFilterService.verificaStatus(statusStr);
        Cliente cliente = clienteGetEntityService.verificarLoginCliente(clienteId, codigoAcesso);
        List<Pedido> pedidosCliente = pedidoRepository.findPedidosByClienteOrderByTimestampDesc(cliente);

        List<Pedido> filteredPedidos = pedidoFilterService.filterPedidosByState(pedidosCliente, status);
        return filteredPedidos.stream()
                .map(pedido -> modelMapper.map(pedido, PedidoResponseDTO.class))
                .collect(toList());
    }
}
