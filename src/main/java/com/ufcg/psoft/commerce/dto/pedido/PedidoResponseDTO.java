package com.ufcg.psoft.commerce.dto.pedido;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.dto.cliente.ClienteGetResponseDTO;
import com.ufcg.psoft.commerce.dto.entregador.EntregadorResponseDTO;
import com.ufcg.psoft.commerce.dto.estabelecimento.EstabelecimentoResponseDTO;
import com.ufcg.psoft.commerce.model.cliente.Endereco;
import com.ufcg.psoft.commerce.model.pedido.ItemVenda;
import com.ufcg.psoft.commerce.model.pagamento.Pagamento;
import com.ufcg.psoft.commerce.model.pedido.statepedido.StatePedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("total")
    private Double total;

    @JsonProperty("itens")
    private List<ItemVenda> itens;

    @JsonProperty("cliente")
    @JsonIgnoreProperties("endereco")
    private ClienteGetResponseDTO cliente;

    @JsonProperty("estabelecimento")
    @JsonIgnoreProperties("codigoAcesso")
    private EstabelecimentoResponseDTO estabelecimento;

    @JsonProperty("entregador")
    @JsonIgnoreProperties("codigoAcesso")
    private EntregadorResponseDTO entregador;

    @JsonProperty("endereco")
    private Endereco endereco;

    @JsonProperty("status")
    @JsonIgnoreProperties("pedido")
    private StatePedido status;

    @JsonProperty("pagamento")
    @JsonIgnoreProperties("pedido")
    private Pagamento pagamento;

    @JsonProperty("ficha")
    private Long ficha;
}
