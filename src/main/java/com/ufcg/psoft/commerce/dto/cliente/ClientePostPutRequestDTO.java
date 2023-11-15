package com.ufcg.psoft.commerce.dto.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.cliente.Endereco;
import com.ufcg.psoft.commerce.validation.ValidSixDigitPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientePostPutRequestDTO {

    @NotBlank(message = "Codigo de Acesso não pode ser vazio.")
    @JsonProperty("codigoAcesso")
    @ValidSixDigitPassword
    private String codigoAcesso;

    @NotBlank(message = "Nome não pode ser vazio.")
    @JsonProperty("nome")
    private String nome;

    @NotBlank(message = "Email não pode ser vazio.")
    @JsonProperty("email")
    private String email;

    @NotNull(message = "Endereço não pode ser vazio.")
    @JsonProperty("endereco")
    private Endereco endereco;
}
