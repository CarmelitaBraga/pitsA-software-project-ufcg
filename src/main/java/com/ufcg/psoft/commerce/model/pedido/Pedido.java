package com.ufcg.psoft.commerce.model.pedido;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.model.cliente.Endereco;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.pagamento.Pagamento;
import com.ufcg.psoft.commerce.model.pedido.statepedido.StatePedido;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "Pedidos")
@JsonIdentityInfo(
        scope = Pedido.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Pedido{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @ElementCollection
    @CollectionTable(name = "pedido_itens",
                     joinColumns = @JoinColumn(name = "pedido_id"))
    @JsonProperty("itens")
    private List<ItemVenda> itens;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    @JsonProperty("cliente")
    private Cliente cliente;

    @OneToOne(cascade =  CascadeType.MERGE)
    @JoinColumn(name = "entregador_id")
    @JsonProperty("entregador")
    private Entregador entregador;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "estabelecimento_id")
    @JsonProperty("estabelecimento")
    private Estabelecimento estabelecimento;

    @Column(name = "total")
    @JsonProperty("total")
    private Double total;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    @JsonProperty("status")
    private StatePedido status;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    @JsonProperty("endereco")
    private Endereco endereco;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "pagamento_id", referencedColumnName = "id")
    @JsonProperty("pagamento")
    private Pagamento pagamento;

    @Column(name = "timestamp")
    @JsonProperty("timestamp")
    @CreationTimestamp
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "orderEstabelecimento_id")
    @JsonProperty("orderEstabelecimento")
    @JsonIgnore
    private Estabelecimento orderEstabelecimento;

    @Column
    @JsonProperty("ficha")
    private Long ficha;


    public static class PedidoBuilder {
        private Double calcularTotal() {
            Double resultado = 0D;
            for (ItemVenda item : itens) resultado += item.getSubTotal();
            return resultado;
        }
        public Pedido build() {
            timestamp = LocalDateTime.now(ZoneId.of("GMT-03:00")).truncatedTo(ChronoUnit.SECONDS);
            Double total = this.calcularTotal();
            this.total = total;
            return new Pedido(this.id, this.itens, this.cliente, this.entregador, this.estabelecimento, this.total,this.status, this.endereco, this.pagamento, this.timestamp, this.orderEstabelecimento, this.ficha);
        }
    }

    public void confirmaPagamento(){
        this.status.confirmaPagamento();
    }

    public void terminoPreparo(){
        this.status.terminoPreparo();
    }

    public void atribuidoEntregador(){
        this.status.atribuidoEntregador();
    }

    public void clienteConfirmaEntrega(){
        this.status.clienteConfirmaEntrega();
    }

    public void cancelaPedido(){
        this.status.clienteCancelaPedido();
    }


}
