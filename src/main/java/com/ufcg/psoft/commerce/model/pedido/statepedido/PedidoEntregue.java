package com.ufcg.psoft.commerce.model.pedido.statepedido;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ufcg.psoft.commerce.exception.pedido.InvalidCancellationException;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("Entregue")
@JsonIdentityInfo(
        scope = PedidoEntregue.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class PedidoEntregue extends StatePedido{

    @Override
    public void clienteCancelaPedido(){
        throw new InvalidCancellationException();
    }
}
