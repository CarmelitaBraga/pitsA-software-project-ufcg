package com.ufcg.psoft.commerce.dto.entregador;

import com.ufcg.psoft.commerce.model.entregador.Veiculo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntregadorPostPutRequestDTO {
    @NotEmpty(message = "Nome não pode ser vazio.")
    private String nome;
    @NotEmpty(message = "Codigo de Acesso não pode ser vazio.")
    private String codigoAcesso;
    @Valid
    private Veiculo veiculo;
    @NotNull(message = "disponiblidade não pode ser vazia!")
    private Boolean disponibilidade;

}