package com.ufcg.psoft.commerce.model.pedido.statepedido;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("Preparo")
@JsonIdentityInfo(
        scope = PedidoEmPreparo.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class PedidoEmPreparo extends StatePedido{

    @Override
    public void terminoPreparo(){
        Pedido pedido = super.getPedido();
        StatePedido pedidoPronto = PedidoPronto.builder().build();
        pedidoPronto.setOrderNumber(3);
        pedidoPronto.setPedido(pedido);
        pedido.setStatus(pedidoPronto);
    }

}
