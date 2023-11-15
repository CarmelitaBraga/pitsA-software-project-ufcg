package com.ufcg.psoft.commerce.service.sabor;

import com.ufcg.psoft.commerce.dto.sabor.SaborResponseDTO;
import com.ufcg.psoft.commerce.model.sabor.Sabor;
import java.util.Collection;

public interface ISaborV1GetService {
    public Sabor buscarUmSabor(Long saborId, Long estabelecimentoId, String codigoAcesso);
    public Collection<SaborResponseDTO> buscarTodosSabores(Long estabelecimentoId, String codigoAcesso);
}
