package com.ufcg.psoft.commerce.service.estabelecimento;


@FunctionalInterface
public interface IEstabelecimentoDeleteService {

    public void excluir(Long id, String codigoAcesso);
}
