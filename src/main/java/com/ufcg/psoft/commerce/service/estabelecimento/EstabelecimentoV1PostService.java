package com.ufcg.psoft.commerce.service.estabelecimento;

import com.ufcg.psoft.commerce.dto.estabelecimento.EstabelecimentoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.estabelecimento.EstabelecimentoResponseDTO;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.repository.EstabelecimentoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstabelecimentoV1PostService implements IEstabelecimentoPostService {

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    ModelMapper modelMapper;

    public EstabelecimentoResponseDTO cadastrar(EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO){
        Estabelecimento estabelecimento = estabelecimentoRepository.save(modelMapper.map(estabelecimentoPostPutRequestDTO, Estabelecimento.class));
        return modelMapper.map(estabelecimento, EstabelecimentoResponseDTO.class);
    }
}
