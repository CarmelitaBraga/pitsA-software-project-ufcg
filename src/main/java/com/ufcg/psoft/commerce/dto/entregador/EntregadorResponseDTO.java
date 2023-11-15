package com.ufcg.psoft.commerce.dto.entregador;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.dto.estabelecimento.EstabelecimentoResponseDTO;
import com.ufcg.psoft.commerce.model.associacao.Associacao;
import com.ufcg.psoft.commerce.model.entregador.Veiculo;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntregadorResponseDTO {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("nome")
    private String nome;
    @JsonProperty("veiculo")
    private Veiculo veiculo;
    @JsonProperty("disponibilidade")
    private Boolean disponibilidade;
    @JsonProperty("associacoes")
    private Set<Associacao> associacoes;
    @JsonProperty("estabelecimento")
    @JsonIgnoreProperties("codigoAcesso")
    private EstabelecimentoResponseDTO estabelecimento;
    @JsonProperty("ficha")
    private Long ficha;
}
