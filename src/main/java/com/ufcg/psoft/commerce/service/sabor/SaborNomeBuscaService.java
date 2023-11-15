package com.ufcg.psoft.commerce.service.sabor;

import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.sabor.Sabor;
import com.ufcg.psoft.commerce.repository.SaborRepository;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoEntityService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class SaborNomeBuscaService implements ISaborNomeBuscaService{
    @Autowired
    private SaborRepository saborRepository;
    @Autowired
    private IEstabelecimentoEntityService estabelecimentoGetEntityService;

    @Override
    public Sabor buscaSaborPeloNome(String nome, Long estabelecimentoId) {
        Estabelecimento estabelecimento = this.estabelecimentoGetEntityService.getEstabelecimento(estabelecimentoId);
        Collection<Sabor> sabores = this.saborRepository.findAll();
        List<Sabor> saboresEstabelecimento =  sabores.stream()
                .filter(sabor -> sabor.getEstabelecimento().equals(estabelecimento))
                .toList();
        List<Sabor> saborNome = saboresEstabelecimento.stream().filter(sabor -> sabor.getNome().equalsIgnoreCase(nome)).toList();
        if(saborNome.isEmpty()){
            return null;
        }
        return saboresEstabelecimento.stream().filter(a -> a.getNome().equalsIgnoreCase(nome)).toList().get(0);
    }
}
