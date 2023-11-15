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
@DiscriminatorValue("P")
@JsonIdentityInfo(
        scope = Pix.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Pix extends TipoPagamento {
    private static final Double DESCONTO = 0.95;
    public Double calculaTotal(Double valorPedido) {
        return valorPedido * DESCONTO;
    }

}
