package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.cliente.ClienteGetResponseDTO;
import java.util.List;

public interface IClienteGetService {
    public List<ClienteGetResponseDTO> retornaTodos();

    public ClienteGetResponseDTO retornaCliente(Long id);
}
