package com.ufcg.psoft.commerce.service.sabor;

import com.ufcg.psoft.commerce.dto.sabor.SaborV2ResponseDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SaborCardapioOrdenaService implements ISaborCardapioOrdenaService{


    @Override
    public List<SaborV2ResponseDTO> ordenaCardapio(List<SaborV2ResponseDTO> cardapio) {
        List<SaborV2ResponseDTO> cardapioOrdenado = new ArrayList<>();
        List<SaborV2ResponseDTO> saboresDisponiveis = cardapio.stream().filter(SaborV2ResponseDTO::getDisponivel).toList();
        List<SaborV2ResponseDTO> saboresindisponiveis = cardapio.stream().filter(a -> !a.getDisponivel()).toList();

        cardapioOrdenado.addAll(saboresDisponiveis);
        cardapioOrdenado.addAll(saboresindisponiveis);

        return cardapioOrdenado;
    }
}
