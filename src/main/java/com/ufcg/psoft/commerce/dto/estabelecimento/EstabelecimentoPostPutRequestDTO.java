package com.ufcg.psoft.commerce.dto.estabelecimento;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.validation.ValidSixDigitPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstabelecimentoPostPutRequestDTO {

    @JsonProperty("codigoAcesso")
    @NotBlank(message = "Codigo obrigatorio!")
    @ValidSixDigitPassword
    private String codigoAcesso;

    @JsonProperty("email")
    @NotBlank(message = "Email obrigatorio!")
    private String email;
}
