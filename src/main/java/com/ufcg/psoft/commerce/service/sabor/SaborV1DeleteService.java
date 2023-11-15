package com.ufcg.psoft.commerce.service.sabor;

import com.ufcg.psoft.commerce.repository.EstabelecimentoRepository;
import com.ufcg.psoft.commerce.repository.SaborRepository;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoEntityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaborV1DeleteService implements ISaborV1DeleteService {
    @Autowired
    SaborRepository saborRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;
    @Autowired
    private SaborGetEntityService saborGetEntityService;
    @Autowired
    private IEstabelecimentoEntityService estabelecimentoEntityService;
    @Override
    public void apagarSabor(Long saborId, Long estabelecimentoId, String codigoAcesso) {
            this.estabelecimentoEntityService.verificarLoginEstabelecimento(estabelecimentoId, codigoAcesso);
            this.saborGetEntityService.verificaSabor(saborId, estabelecimentoId);
            this.saborRepository.deleteById(saborId);
        }
    }

