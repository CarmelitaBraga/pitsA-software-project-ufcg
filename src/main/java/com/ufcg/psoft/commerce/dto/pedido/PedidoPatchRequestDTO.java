package com.ufcg.psoft.commerce.dto.pedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PedidoPatchRequestDTO {

    private Character tipoPagamento;
}
