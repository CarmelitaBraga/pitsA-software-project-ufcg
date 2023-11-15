package com.ufcg.psoft.commerce.service.sabor;

import com.ufcg.psoft.commerce.model.sabor.Sabor;

public interface ISaborNomeBuscaService {
    public Sabor buscaSaborPeloNome(String nome, Long estabelecimentoId);
}
