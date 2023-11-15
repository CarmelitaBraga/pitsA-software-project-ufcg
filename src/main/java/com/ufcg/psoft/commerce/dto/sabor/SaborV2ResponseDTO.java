package com.ufcg.psoft.commerce.dto.sabor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaborV2ResponseDTO {

    @JsonProperty("nome")
    private String nome;
    @JsonProperty("precoM")
    private Double precoM;
    @JsonProperty("precoG")
    private Double precoG;
    @JsonProperty("tipo")
    private Character tipo;
    @JsonProperty("disponivel")
    private Boolean disponivel;
}
