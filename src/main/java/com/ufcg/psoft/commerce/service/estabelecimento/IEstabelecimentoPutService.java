package com.ufcg.psoft.commerce.service.estabelecimento;


import com.ufcg.psoft.commerce.dto.estabelecimento.EstabelecimentoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.estabelecimento.EstabelecimentoResponseDTO;

@FunctionalInterface
public interface IEstabelecimentoPutService {
    public EstabelecimentoResponseDTO atualizar(Long id, String codigoAcesso, EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO);
}
