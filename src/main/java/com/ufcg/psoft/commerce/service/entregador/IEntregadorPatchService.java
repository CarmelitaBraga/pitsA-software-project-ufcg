package com.ufcg.psoft.commerce.service.entregador;

import com.ufcg.psoft.commerce.dto.entregador.EntregadorPatchDto;
import com.ufcg.psoft.commerce.dto.entregador.EntregadorResponseDTO;
import com.ufcg.psoft.commerce.model.entregador.Entregador;

@FunctionalInterface
public interface IEntregadorPatchService {
    public EntregadorResponseDTO atualizaDisponibilidade(EntregadorPatchDto entregadorPatchDto, Long id , String codigoAcesso) throws Exception;
}
