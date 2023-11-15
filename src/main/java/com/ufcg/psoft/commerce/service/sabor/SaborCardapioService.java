package com.ufcg.psoft.commerce.service.sabor;

import com.ufcg.psoft.commerce.dto.sabor.SaborV2ResponseDTO;
import com.ufcg.psoft.commerce.exception.sabor.InvalidSaborTypeException;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.sabor.Sabor;
import com.ufcg.psoft.commerce.repository.SaborRepository;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoEntityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class SaborCardapioService implements ISaborCardapioService{
    @Autowired
    private SaborRepository saborRepository;
    @Autowired
    private ISaborCardapioOrdenaService saborCardapioOrdenaService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IEstabelecimentoEntityService estabelecimentoGetEntityService;

    @Override
    public List<SaborV2ResponseDTO> buscarCardapio(Long estabelecimentoId) {
        Estabelecimento estabelecimento = this.estabelecimentoGetEntityService.getEstabelecimento(estabelecimentoId);
        
        Collection<Sabor> sabores = this.saborRepository.findAll();
        List<Sabor> cardapioFiltrado =  sabores.stream()
                .filter(sabor -> sabor.getEstabelecimento().equals(estabelecimento))
                .toList();
        List<SaborV2ResponseDTO> output = new ArrayList<>();
        for(Sabor sabor: cardapioFiltrado){
            output.add(this.modelMapper.map(sabor, SaborV2ResponseDTO.class));
        }

        return saborCardapioOrdenaService.ordenaCardapio(output);
    }

    @Override
    public List<SaborV2ResponseDTO> buscarCardapioTipo(Long estabelecimentoId, Character tipo) {
        Estabelecimento estabelecimento = this.estabelecimentoGetEntityService.getEstabelecimento(estabelecimentoId);

        Character a = Character.valueOf('S');
        Character b = Character.valueOf('D');

        if((a.equals(Character.toUpperCase(tipo)) || b.equals(Character.toUpperCase(tipo)))){
            Collection<Sabor> sabores = this.saborRepository.findAll();
            List<Sabor> cardapioFiltrado =  sabores.stream()
                    .filter(sabor -> sabor.getEstabelecimento().equals(estabelecimento))
                    .toList()
                    .stream()
                    .filter(sabor -> sabor.getTipo().equals(Character.toUpperCase(tipo)))
                    .toList();

            List<SaborV2ResponseDTO> output = new ArrayList<>();
            for(Sabor sabor: cardapioFiltrado){
                output.add(this.modelMapper.map(sabor, SaborV2ResponseDTO.class));
            }
            return saborCardapioOrdenaService.ordenaCardapio(output);
        }else{
            throw new InvalidSaborTypeException();
        }
    }

}
