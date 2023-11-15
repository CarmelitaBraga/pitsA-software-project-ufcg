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
public class SaborV1PutService implements ISaborV1PutService {
    @Autowired
    SaborRepository saborRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private IEstabelecimentoEntityService estabelecimentoEntityService;
    @Autowired
    private SaborGetEntityService saborGetEntityService;

    @Override
    public SaborResponseDTO atualizarSabor(Long saborId, Long estabelecimentoId, String codigoAcesso, SaborPostPutRequestDTO saborPostPutRequestDTO) {
        Estabelecimento estabelecimento = estabelecimentoEntityService.verificarLoginEstabelecimento(estabelecimentoId, codigoAcesso);
        this.saborGetEntityService.verificaSabor(saborId, estabelecimentoId);

        Sabor sabor1 = this.modelMapper.map(saborPostPutRequestDTO, Sabor.class);
        sabor1.setId(this.saborRepository.findSaborById(saborId).getId());
        sabor1.setEstabelecimento(estabelecimento);
        Sabor sabor2 = this.saborRepository.save(sabor1);
        return this.modelMapper.map(sabor2, SaborResponseDTO.class);
    }
}
