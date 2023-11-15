package com.ufcg.psoft.commerce.dto.cliente;


import com.ufcg.psoft.commerce.validation.ValidSixDigitPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClienteInteresseRequestDTO {

    @NotBlank(message = "Sabor não pode ser vazio.")
    private String sabor;
    @NotNull(message = "Id de estabelecimento não pode ser vazio.")
    private Long idEstabelecimento;
    @NotNull(message = "Id de cliente não pode ser vazio.")
    private Long idCliente;
    @ValidSixDigitPassword
    private String codigoAcesso;


}
