package com.ufcg.psoft.commerce.model.pagamento;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import jakarta.persistence.*;
import lombok.*;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Pagamentos")
@JsonIdentityInfo(
        scope = Pagamento.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Pagamento {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private boolean pago;
    @Column
    @JsonProperty("valorPagamento")
    private Double valorPagamento;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tipoPagamento_id", referencedColumnName = "id")
    private TipoPagamento tipoPagamento;
    @OneToOne
    private Pedido pedido;

    public void efetuaPagamento(){
        this.pago = true;
    }

    public void setValorPagamento(Double valorPedido){
        this.valorPagamento = this.tipoPagamento.calculaTotal(valorPedido);
    }
}
