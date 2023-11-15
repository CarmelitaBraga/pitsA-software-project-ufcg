package com.ufcg.psoft.commerce.dto.sabor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.validation.ValidSaborType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaborPostPutRequestDTO {
    @NotEmpty(message = "nome não pode ser nulo ou vazio")
    @JsonProperty("nome")
    private String nome;

    @ValidSaborType
    @JsonProperty("tipo")
    private Character tipo;

    @NotNull(message = "precoM não pode ser nulo")
    @PositiveOrZero(message = "PrecoM deve ser maior que zero")
    @JsonProperty("precoM")
    private Double precoM;

    @NotNull(message = "precoG não pode ser nulo")
    @PositiveOrZero(message = "PrecoG deve ser maior que zero")
    @JsonProperty("precoG")
    private Double precoG;

    @NotNull(message = "disponivel não pode ser nulo")
    @JsonProperty("disponivel")
    private Boolean disponivel;
    
    @JsonProperty("estabelecimento")
    private Estabelecimento estabelecimento;
}
