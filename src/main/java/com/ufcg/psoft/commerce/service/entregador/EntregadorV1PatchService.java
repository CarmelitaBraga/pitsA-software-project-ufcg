package com.ufcg.psoft.commerce.service.entregador;

import com.ufcg.psoft.commerce.dto.entregador.EntregadorPatchDto;
import com.ufcg.psoft.commerce.dto.entregador.EntregadorResponseDTO;
import com.ufcg.psoft.commerce.exception.associacao.AssociacaoEntregadorNotFoundException;
import com.ufcg.psoft.commerce.model.associacao.Associacao;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.repository.AssociacaoRepository;
import com.ufcg.psoft.commerce.repository.EntregadorRepository;
import com.ufcg.psoft.commerce.service.order.CommandOrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntregadorV1PatchService implements IEntregadorPatchService {
    @Autowired
    private EntregadorRepository entregadorRepository;
    @Autowired
    private EntregadorV1LoginService entregadorLoginService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private AssociacaoRepository associacaoRepository;

    @Autowired
    private CommandOrderService commandOrderService;
    @Override
    public EntregadorResponseDTO atualizaDisponibilidade(EntregadorPatchDto entregadorPatchDto, Long id, String codigoAcesso) throws Exception {
        Entregador entregador =  entregadorLoginService.verificarLogin(id, codigoAcesso);

        List<Associacao> associacaoAprovada = associacaoRepository.findAssociacaosByEntregadorAndStatusTrue(entregador);
        if(entregadorPatchDto.getDisponibilidade() && associacaoAprovada.isEmpty()){
            throw new AssociacaoEntregadorNotFoundException();
        }

        if(entregadorPatchDto.getDisponibilidade()){
            commandOrderService.adicionaEntregadorFila(entregador);
        }else{
            commandOrderService.removeEntregadorFila(entregador);
        }

        entregador.setDisponibilidade(entregadorPatchDto.getDisponibilidade());
        this.entregadorRepository.save(entregador);
        return modelMapper.map(this.entregadorRepository.findEntregadorById(id), EntregadorResponseDTO.class);
    }
}
