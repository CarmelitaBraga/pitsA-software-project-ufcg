package com.ufcg.psoft.commerce.service.estabelecimento;

import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;

public interface IEstabelecimentoEntityService {
    public Estabelecimento getEstabelecimento(Long id);

    public Estabelecimento verificarLoginEstabelecimento(Long estabelecimentoId, String codigoAcesso);
}
