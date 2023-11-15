package com.ufcg.psoft.commerce.model.entregador;

import com.ufcg.psoft.commerce.validation.ValidVehicleType;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Veiculo {

    @NotBlank(message = "Placa do veiculo e obrigatoria")
    private String placa;
    @NotBlank( message = "Cor do veiculo e obrigatoria")
    private String cor;
    @NotBlank(message = "Tipo do veiculo e obrigatorio")
    @ValidVehicleType
    private String tipo;

}
