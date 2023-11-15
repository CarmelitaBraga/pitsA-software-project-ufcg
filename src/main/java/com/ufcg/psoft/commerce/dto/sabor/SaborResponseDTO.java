package com.ufcg.psoft.commerce.dto.sabor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaborResponseDTO {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("nome")
    private String nome;
    @JsonProperty("tipo")
    private Character tipo;

    @JsonProperty("precoM")
    private Double precoM;
    @JsonProperty("precoG")
    private Double precoG;
    @JsonProperty("disponivel")
    private Boolean disponivel;
    @JsonProperty("estabelecimento")
    private Estabelecimento estabelecimento;
    @JsonProperty("clientes")
    @Builder.Default
    private Set<Cliente> clientes = new HashSet<>();
}
