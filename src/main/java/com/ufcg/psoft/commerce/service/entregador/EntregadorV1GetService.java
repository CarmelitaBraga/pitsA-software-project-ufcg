package com.ufcg.psoft.commerce.service.entregador;

import com.ufcg.psoft.commerce.dto.entregador.EntregadorResponseDTO;
import com.ufcg.psoft.commerce.exception.entregador.EntregadorNotFoundException;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.repository.EntregadorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EntregadorV1GetService implements IEntregadorGetService {
    @Autowired
    private EntregadorRepository entregadorRepository;
    @Autowired
    private ModelMapper modelMapper;


    public EntregadorResponseDTO getOne(Long id) throws Exception{
        Entregador entregadorResultante = this.entregadorRepository.findEntregadorById(id);

        if (entregadorResultante == null) {
            throw new EntregadorNotFoundException();
        }
        EntregadorResponseDTO entregadorResponseDTO = this.modelMapper.map(entregadorResultante, EntregadorResponseDTO.class);
        return entregadorResponseDTO;
    }

    @Override
    public List<EntregadorResponseDTO> getAll(){
      List<Entregador> entregadores = this.entregadorRepository.findAll();
      List<EntregadorResponseDTO> entregadoresGetDto = new ArrayList<>();
      for(Entregador entregador: entregadores){
          entregadoresGetDto.add(this.modelMapper.map(entregador, EntregadorResponseDTO.class));
      }
        return entregadoresGetDto;
    }

}
