package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.cliente.ClienteInteresseRequestDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClienteInteresseResponseDTO;

@FunctionalInterface
public interface IClientePatchInteresseService {

    public ClienteInteresseResponseDTO addInteresse(ClienteInteresseRequestDTO clienteInteresseRequestDTO);
}
