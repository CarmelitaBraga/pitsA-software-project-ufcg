package com.ufcg.psoft.commerce.service.estabelecimento;

import com.ufcg.psoft.commerce.dto.estabelecimento.EstabelecimentoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.estabelecimento.EstabelecimentoResponseDTO;
import com.ufcg.psoft.commerce.exception.CodigoInvalidoException;
import com.ufcg.psoft.commerce.exception.estabelecimento.EstabelecimentoNotFound;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.repository.EstabelecimentoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstabelecimentoV1PutService implements IEstabelecimentoPutService {

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public EstabelecimentoResponseDTO atualizar(Long id, String codigoAcesso,EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO){
        Estabelecimento estabelecimento = estabelecimentoRepository.findEstabelecimentoById(id);
        if(estabelecimento == null){
            throw new EstabelecimentoNotFound();
        }

        if(!(estabelecimento.getCodigoAcesso().equals(codigoAcesso))){
            throw new CodigoInvalidoException();
        }
        Estabelecimento estabelecimentoAux = modelMapper.map(estabelecimentoPostPutRequestDTO, Estabelecimento.class);
        estabelecimentoAux.setId(id);
        return modelMapper.map(estabelecimentoRepository.save(estabelecimentoAux), EstabelecimentoResponseDTO.class);
    }
}
