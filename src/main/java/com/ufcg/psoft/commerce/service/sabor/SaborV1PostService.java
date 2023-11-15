package com.ufcg.psoft.commerce.service.sabor;

import com.ufcg.psoft.commerce.dto.sabor.SaborPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.sabor.SaborResponseDTO;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.model.sabor.Sabor;
import com.ufcg.psoft.commerce.repository.SaborRepository;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoEntityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaborV1PostService implements ISaborV1PostService {
    @Autowired
    private SaborRepository saborRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IEstabelecimentoEntityService estabelecimentoEntityService;
    @Override
    public SaborResponseDTO cadastrarSabor(Long estabelecimentoId, String codigoAcesso, SaborPostPutRequestDTO saborPostPutRequestDTO) {
        Estabelecimento estabelecimento = estabelecimentoEntityService.verificarLoginEstabelecimento(estabelecimentoId, codigoAcesso);
        Sabor sabor = this.modelMapper.map(saborPostPutRequestDTO, Sabor.class);
        sabor.setDisponivel(true);
        sabor.setEstabelecimento(estabelecimento);
        Sabor saborAtualizado = this.saborRepository.save(sabor);
        return this.modelMapper.map(saborAtualizado, SaborResponseDTO.class);
    }
}
