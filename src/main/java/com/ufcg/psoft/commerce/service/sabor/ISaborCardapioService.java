package com.ufcg.psoft.commerce.service.sabor;

import com.ufcg.psoft.commerce.dto.sabor.SaborV2ResponseDTO;
import java.util.List;

public interface ISaborCardapioService {
    public List<SaborV2ResponseDTO> buscarCardapio(Long estabelecimentoId);
    public List<SaborV2ResponseDTO> buscarCardapioTipo(Long estabelecimentoId, Character tipo);
}
