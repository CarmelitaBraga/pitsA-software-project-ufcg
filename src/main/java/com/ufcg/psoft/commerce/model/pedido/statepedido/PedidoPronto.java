package com.ufcg.psoft.commerce.model.pedido.statepedido;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ufcg.psoft.commerce.exception.pedido.InvalidCancellationException;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.model.entregador.Veiculo;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("Pronto")
@JsonIdentityInfo(
        scope = PedidoPronto.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class PedidoPronto extends StatePedido{

    @Override
    public void atribuidoEntregador(){
        Pedido pedido = super.getPedido();
        StatePedido pedidoEmRota = PedidoEmRota.builder().build();
        pedidoEmRota.setOrderNumber(4);
        pedidoEmRota.setPedido(pedido);
        pedido.setStatus(pedidoEmRota);
    }

    @Override
    public void clienteCancelaPedido(){
        throw new InvalidCancellationException();
    }
}
