
package com.ufcg.psoft.commerce.service.order;


import com.ufcg.psoft.commerce.model.associacao.Associacao;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.repository.AssociacaoRepository;
import com.ufcg.psoft.commerce.repository.EstabelecimentoRepository;
import com.ufcg.psoft.commerce.service.Notificacao.NotificacaoEnviarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandOrderService implements ICommandOrderService{

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    private AssociacaoRepository associacaoRepository;

    @Autowired
    private NotificacaoEnviarService notificacaoEnviarService;

    private void executa(Estabelecimento estabelecimento){
        List<Entregador> filaEntregador = estabelecimento.getFilaEntregador();
        List<Pedido> filaPedido = estabelecimento.getFilaPedidos();

        while(!filaEntregador.isEmpty() && !filaPedido.isEmpty()){
            Entregador entregador = filaEntregador.remove(0);

            Pedido pedido = filaPedido.remove(0);
            pedido.setFicha(null);
            entregador.setFicha(null);

            pedido.setEntregador(entregador);
            pedido.atribuidoEntregador();
        }

    }


    public void adicionaEntregadorFila(Entregador entregador){
        List<Associacao> listaAssociacoes = associacaoRepository.findAssociacaosByEntregadorAndStatusTrue(entregador);
        Associacao associacao = listaAssociacoes.get(0);
        List<Entregador> filaEntregador = associacao.getEstabelecimento().getFilaEntregador();
        if(filaEntregador.isEmpty()){
            entregador.setFicha(1L);
        }
        else{
            Entregador ultimoEntregador = filaEntregador.get(filaEntregador.size()-1);
            entregador.setFicha(ultimoEntregador.getFicha()+1);
        }
        filaEntregador.add(entregador);
        entregador.setEstabelecimento(associacao.getEstabelecimento());

        executa(associacao.getEstabelecimento());
    }

    public void adicionaPedidoFila(Pedido pedido){
        List<Pedido> filaPedido = pedido.getEstabelecimento().getFilaPedidos();

        if(filaPedido.isEmpty()){
            pedido.setFicha(1L);
        }
        else{
            Pedido ultimoPedido = filaPedido.get(filaPedido.size()-1);
            pedido.setFicha(ultimoPedido.getFicha()+1);
        }
        filaPedido.add(pedido);
        if(pedido.getEstabelecimento().getFilaEntregador().isEmpty()){
            this.notificaCliente(pedido);
        }
        executa(pedido.getEstabelecimento());
    }

    public void removeEntregadorFila(Entregador entregador){
        List<Associacao> lista = associacaoRepository.findAssociacaosByEntregadorAndStatusTrue(entregador);
        Associacao associacao = lista.get(0);
        associacao.getEstabelecimento().getFilaEntregador().remove(entregador);
        entregador.setFicha(null);
    }

    private void notificaCliente(Pedido pedido) {
        Cliente cliente = pedido.getCliente();

        String assunto = "Sentimos muito! Seu pedido está em espera!";
        String texto = "O pedido #" + pedido.getId() + " está em espera por falta de entregador do estabelecimento!";

        this.notificacaoEnviarService.enviarEmail(cliente.getEmail(), assunto, texto);

    }
}

