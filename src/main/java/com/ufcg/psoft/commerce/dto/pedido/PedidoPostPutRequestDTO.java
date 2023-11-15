package com.ufcg.psoft.commerce.dto.pedido;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.pedido.ItemVenda;
import com.ufcg.psoft.commerce.model.cliente.Endereco;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoPostPutRequestDTO {
    @NotNull(message = "Lista de itens não pode ser vazia!")
    @JsonProperty("itens")
    private List<@Valid ItemVenda> itens;

    @JsonProperty("endereco")
    private Endereco endereco;
}
