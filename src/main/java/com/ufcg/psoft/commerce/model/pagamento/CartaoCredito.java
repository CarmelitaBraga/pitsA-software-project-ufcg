package com.ufcg.psoft.commerce.model.pagamento;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("C")
@JsonIdentityInfo(
        scope = CartaoCredito.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class CartaoCredito extends TipoPagamento {

    public Double calculaTotal(Double valorPedido) {
        return valorPedido;
    }
}