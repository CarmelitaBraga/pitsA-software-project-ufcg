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
@DiscriminatorValue("Recebido")
@JsonIdentityInfo(
        scope = PedidoRecebido.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class PedidoRecebido extends StatePedido{


    @Override
    public void confirmaPagamento(){
        Pedido pedido = super.getPedido();
        StatePedido pedidoEmPreparo = PedidoEmPreparo.builder().build();
        pedidoEmPreparo.setOrderNumber(2);
        pedidoEmPreparo.setPedido(pedido);
        pedido.setStatus(pedidoEmPreparo);
    }



}
