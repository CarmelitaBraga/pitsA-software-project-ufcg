package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.cliente.ClienteInteresseRequestDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClienteInteresseResponseDTO;
import com.ufcg.psoft.commerce.exception.sabor.InvalidInteresseException;
import com.ufcg.psoft.commerce.exception.sabor.SaborNotFoundException;
import com.ufcg.psoft.commerce.model.cliente.Cliente;
import com.ufcg.psoft.commerce.model.sabor.Sabor;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoEntityService;
import com.ufcg.psoft.commerce.service.sabor.ISaborNomeBuscaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteV1InteresseService implements IClientePatchInteresseService {
    @Autowired
    ISaborNomeBuscaService saborNomeBuscaService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private IEstabelecimentoEntityService estabelecimentoGetEntityService;

    @Autowired
    private IClienteEntityService clienteGetEntityService;

    @Override
    public ClienteInteresseResponseDTO addInteresse(ClienteInteresseRequestDTO clienteInteresseRequestDTO) {
        Cliente cliente = this.clienteGetEntityService.verificarLoginCliente(
                clienteInteresseRequestDTO.getIdCliente(),
                clienteInteresseRequestDTO.getCodigoAcesso());

        Long idEstabelecimento = clienteInteresseRequestDTO.getIdEstabelecimento();
        this.estabelecimentoGetEntityService.getEstabelecimento(idEstabelecimento);

        Sabor sabor = saborNomeBuscaService.buscaSaborPeloNome(clienteInteresseRequestDTO.getSabor(), idEstabelecimento);

        if(sabor == null){
            throw new SaborNotFoundException();
        }
        if(sabor.getDisponivel()){
            throw new InvalidInteresseException();
        }

        cliente.getSaboresDeInteresse().add(sabor);
        sabor.getClientes().add(cliente);

        ClienteInteresseResponseDTO clienteInteresseResponseDTO = new ClienteInteresseResponseDTO();
        clienteInteresseResponseDTO.setNome(cliente.getNome());
        clienteInteresseResponseDTO.setInteresses(cliente.getSaboresDeInteresse());

        return clienteInteresseResponseDTO;
    }
}
