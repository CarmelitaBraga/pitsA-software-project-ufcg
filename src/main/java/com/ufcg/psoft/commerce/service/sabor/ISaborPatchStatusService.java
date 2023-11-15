package com.ufcg.psoft.commerce.service.sabor;

import com.ufcg.psoft.commerce.dto.sabor.SaborPatchStatusDTO;
import com.ufcg.psoft.commerce.dto.sabor.SaborResponseDTO;

@FunctionalInterface
public interface ISaborPatchStatusService {
    public SaborResponseDTO atualizarSaborStatus(Long saborId, Long estabelecimentoId, String codigoAcesso, SaborPatchStatusDTO saborPatchStatusDTO);
}
