package com.ufcg.psoft.commerce.service.sabor;

import com.ufcg.psoft.commerce.dto.sabor.SaborResponseDTO;
import com.ufcg.psoft.commerce.model.sabor.Sabor;
import com.ufcg.psoft.commerce.repository.SaborRepository;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoEntityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.HashSet;

@Service
public class SaborV1GetService implements ISaborV1GetService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SaborRepository saborRepository;

    @Autowired
    private SaborGetEntityService saborGetEntityService;

    @Autowired
    private IEstabelecimentoEntityService iEstabelecimentoEntityService;
    @Override
    public Sabor buscarUmSabor(Long saborId, Long estabelecimentoId, String codigoAcesso) {
        this.iEstabelecimentoEntityService.verificarLoginEstabelecimento(estabelecimentoId, codigoAcesso);
        return this.saborGetEntityService.verificaSabor(saborId, estabelecimentoId);
    }

    @Override
    public Collection<SaborResponseDTO> buscarTodosSabores(Long estabelecimentoId, String codigoAcesso) {
        this.iEstabelecimentoEntityService.verificarLoginEstabelecimento(estabelecimentoId, codigoAcesso);

        Collection<Sabor> sabores = this.saborRepository.findAll();
        Collection<SaborResponseDTO> output = new HashSet<>();
        for(Sabor sabor : sabores){
            output.add(this.modelMapper.map(sabor, SaborResponseDTO.class));
        }
        return output;
    }
}
