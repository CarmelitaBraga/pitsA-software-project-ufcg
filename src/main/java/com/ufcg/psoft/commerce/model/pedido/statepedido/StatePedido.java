package com.ufcg.psoft.commerce.model.pedido.statepedido;

import com.fasterxml.jackson.annotation.*;
import com.ufcg.psoft.commerce.exception.pedido.IllegalStateChangeException;
import com.ufcg.psoft.commerce.model.pedido.Pedido;

import jakarta.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "state_pedido")
@EqualsAndHashCode(exclude = "pedido")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PedidoEmPreparo.class, name = "PedidoEmPreparo"),
        @JsonSubTypes.Type(value = PedidoEmRota.class, name = "PedidoEmRota"),
        @JsonSubTypes.Type(value = PedidoEntregue.class, name = "PedidoEntregue"),
        @JsonSubTypes.Type(value = PedidoPronto.class, name = "PedidoPronto"),
        @JsonSubTypes.Type(value = PedidoRecebido.class, name = "PedidoRecebido"),

})
@JsonIdentityInfo(
        scope = StatePedido.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public abstract class StatePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @OneToOne
    @JsonProperty("pedido")
    private Pedido pedido;

    @Column(name = "order_number")
    protected Integer orderNumber;

    public void confirmaPagamento(){
        throw new IllegalStateChangeException();
    }

    public void terminoPreparo(){
        throw new IllegalStateChangeException();
    }

    public void atribuidoEntregador(){
        throw new IllegalStateChangeException();
    }

    public void clienteConfirmaEntrega(){
        throw new IllegalStateChangeException();
    }

    public void clienteCancelaPedido(){
    }


}
