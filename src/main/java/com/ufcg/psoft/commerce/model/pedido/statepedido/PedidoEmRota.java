package com.ufcg.psoft.commerce.model.pedido.statepedido;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ufcg.psoft.commerce.exception.pedido.InvalidCancellationException;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("Rota")
@JsonIdentityInfo(
        scope = PedidoEmRota.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class PedidoEmRota extends StatePedido{

    @Override
    public void clienteConfirmaEntrega(){
        Pedido pedido = super.getPedido();
        StatePedido pedidoEntregue = PedidoEntregue.builder().build();
        pedidoEntregue.setOrderNumber(5);
        pedidoEntregue.setPedido(pedido);
        pedido.setStatus(pedidoEntregue);
    }

    @Override
    public void clienteCancelaPedido(){
        throw new InvalidCancellationException();
    }
}
