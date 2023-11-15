package com.ufcg.psoft.commerce.service.sabor;

import com.ufcg.psoft.commerce.dto.sabor.SaborV2ResponseDTO;

import java.util.List;

@FunctionalInterface
public interface ISaborCardapioOrdenaService {

    public List<SaborV2ResponseDTO> ordenaCardapio(List<SaborV2ResponseDTO> cardapio);
}
