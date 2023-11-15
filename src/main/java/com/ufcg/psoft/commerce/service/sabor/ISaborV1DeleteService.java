package com.ufcg.psoft.commerce.service.sabor;
@FunctionalInterface
public interface ISaborV1DeleteService {
    public void apagarSabor(Long saborId, Long estabelecimentoId, String codigoAcesso);
}
