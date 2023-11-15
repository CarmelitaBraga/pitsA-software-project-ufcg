package com.ufcg.psoft.commerce.service.sabor;

import com.ufcg.psoft.commerce.dto.sabor.SaborPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.sabor.SaborResponseDTO;


@FunctionalInterface
public interface ISaborV1PostService {
    public SaborResponseDTO cadastrarSabor(Long estabelecimentoId, String codigoAcesso, SaborPostPutRequestDTO saborPostPutRequestDTO);
}
