package com.ufcg.psoft.commerce.service.sabor;

import com.ufcg.psoft.commerce.dto.sabor.SaborPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.sabor.SaborResponseDTO;

@FunctionalInterface
public interface ISaborV1PutService {
    public SaborResponseDTO atualizarSabor(Long saborId, Long estabelecimentoId, String codigoAcesso, SaborPostPutRequestDTO saborPostPutRequestDTO);
}
