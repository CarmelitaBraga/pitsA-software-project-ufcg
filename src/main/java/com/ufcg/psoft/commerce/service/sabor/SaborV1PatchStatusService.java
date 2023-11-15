package com.ufcg.psoft.commerce.service.sabor;

import com.ufcg.psoft.commerce.dto.sabor.SaborPatchStatusDTO;
import com.ufcg.psoft.commerce.dto.sabor.SaborResponseDTO;
import com.ufcg.psoft.commerce.exception.sabor.SaborStatusIsAlreadyFalseException;
import com.ufcg.psoft.commerce.exception.sabor.SaborStatusIsAlreadyTrueException;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.model.sabor.Sabor;
import com.ufcg.psoft.commerce.repository.SaborRepository;
import com.ufcg.psoft.commerce.service.Notificacao.NotificacaoEnviarService;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class SaborV1PatchStatusService implements ISaborPatchStatusService{
    @Autowired
    private SaborRepository saborRepository;

    @Autowired
    private IEstabelecimentoEntityService estabelecimentoEntityService;

    @Autowired
    private NotificacaoEnviarService notificacaoEnviarService;

    @Autowired
    private SaborGetEntityService saborGetEntityService;

    @Override
    public SaborResponseDTO atualizarSaborStatus(Long saborId, Long estabelecimentoId, String codigoAcesso, SaborPatchStatusDTO saborPatchStatusDTO) {
        estabelecimentoEntityService.verificarLoginEstabelecimento(estabelecimentoId, codigoAcesso);

        Sabor sabor = this.saborGetEntityService.verificaSabor(saborId, estabelecimentoId);

        if(sabor.getDisponivel() && saborPatchStatusDTO.getDisponivel()){
            throw new SaborStatusIsAlreadyTrueException();
        }
        if(!sabor.getDisponivel() && !saborPatchStatusDTO.getDisponivel()){
            throw new SaborStatusIsAlreadyFalseException();
        }

        sabor.setDisponivel(saborPatchStatusDTO.getDisponivel());
        Sabor sabor1 = this.saborRepository.save(sabor);

        if(saborPatchStatusDTO.getDisponivel()){
            this.notificaCliente(sabor);
        }
        return SaborResponseDTO.builder()
                .id(sabor1.getId())
                .clientes(sabor1.getClientes())
                .disponivel(sabor1.getDisponivel())
                .tipo(sabor1.getTipo())
                .precoM(sabor1.getPrecoM())
                .estabelecimento(sabor1.getEstabelecimento())
                .nome(sabor1.getNome())
                .precoG(sabor1.getPrecoG())
                .build();
    }

    private void notificaCliente(Sabor sabor) {
        List<Cliente> clientes = sabor.getClientes().stream().toList();

        String assunto = "O QUE VOCÊ TANTO AGUARDAVA AGORA ESTÁ DISPONÍVEL!";
        String texto = "O ESTABELECIMENTO " + sabor.getEstabelecimento().getId() +
                " ACABA DE DISPONIBILIZAR O SABOR " + sabor.getNome().toUpperCase() +
                ". APROVEITE E PEÇA AGORA!";

        for(Cliente c : clientes){
            notificacaoEnviarService.enviarEmail(c.getEmail(), assunto, texto);

            c.getSaboresDeInteresse().remove(sabor);
        }

        sabor.setClientes(new HashSet<>());
    }
}
