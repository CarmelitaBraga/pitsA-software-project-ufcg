package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.exception.entregador.EntregadorIsUnavailableException;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.model.entregador.Veiculo;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.service.Notificacao.NotificacaoEnviarService;
import com.ufcg.psoft.commerce.service.entregador.IEntregadorEntityService;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoEntityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;

@Service
public class PedidoV1PatchAtribuirEntregadorService implements IPedidoPatchAtribuirEntregadorService {
    @Autowired
    private IPedidoGetEntityService pedidoGetEntityService;
    @Autowired
    private IEstabelecimentoEntityService estabelecimentoEntityService;
    @Autowired
    private IEntregadorEntityService entregadorEntityService;

    @Autowired
    private NotificacaoEnviarService notificacaoEnviarService;
    @Autowired
    private ModelMapper modelMapper;


    @Override
    public PedidoResponseDTO atribuirEntregador(Long idEstabelecimento, String codigoAcesso, Long idPedido, Long idEntregador){
        estabelecimentoEntityService.verificarLoginEstabelecimento(idEstabelecimento, codigoAcesso);
        Pedido pedido = pedidoGetEntityService.validaPedidoEstabelecimento(idPedido, idEstabelecimento);
        Entregador entregador = entregadorEntityService.validarEntregadorEstabelecimento(idEntregador, idEstabelecimento);

        if (!entregador.getDisponibilidade()) throw new EntregadorIsUnavailableException();

        pedido.setEntregador(entregador);
        pedido.atribuidoEntregador();
        this.notificaCliente(pedido, entregador);

        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }

    private void notificaCliente(Pedido pedido, Entregador entregador) {
        Cliente cliente = pedido.getCliente();
        Veiculo veiculo = entregador.getVeiculo();

        String assunto = "AÍ SIM! SEU PEDIDO JÁ ESTÁ EM ROTA";
        String texto = "---------------------" +
                "\nInformações do Pedido" +
                "\n---------------------" +
                "\nStatus: Pedido em Rota" +
                "\nNome do Cliente:" + cliente.getNome() +
                "\nNome do Entregador:" + entregador.getNome() +
                "\nVEÍCULO" +
                "\n Placa:" + veiculo.getPlaca() +
                "\n Tipo:" + veiculo.getTipo() +
                "\n Cor:" + veiculo.getCor();

        notificacaoEnviarService.enviarEmail(cliente.getEmail(), assunto, texto);

    }
}
