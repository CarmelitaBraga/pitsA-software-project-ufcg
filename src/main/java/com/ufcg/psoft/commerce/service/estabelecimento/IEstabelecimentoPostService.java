package com.ufcg.psoft.commerce.service.estabelecimento;

import com.ufcg.psoft.commerce.dto.estabelecimento.EstabelecimentoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.estabelecimento.EstabelecimentoResponseDTO;

@FunctionalInterface
public interface IEstabelecimentoPostService {

    public EstabelecimentoResponseDTO cadastrar(EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO);
}
