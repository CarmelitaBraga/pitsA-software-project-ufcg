package com.ufcg.psoft.commerce.service.entregador;

import com.ufcg.psoft.commerce.dto.entregador.EntregadorResponseDTO;

import java.util.List;

public interface IEntregadorGetService {

    public EntregadorResponseDTO getOne(Long id) throws Exception;
    public List<EntregadorResponseDTO> getAll();
}
