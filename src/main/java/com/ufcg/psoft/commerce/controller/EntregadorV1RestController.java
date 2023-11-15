package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.entregador.EntregadorResponseDTO;
import com.ufcg.psoft.commerce.dto.entregador.EntregadorPatchDto;
import com.ufcg.psoft.commerce.dto.entregador.EntregadorPostPutRequestDTO;
import com.ufcg.psoft.commerce.model.entregador.Entregador;
import com.ufcg.psoft.commerce.service.entregador.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(value = "/entregadores",  produces = MediaType.APPLICATION_JSON_VALUE)
public class EntregadorV1RestController {
    @Autowired
    private EntregadorV1PostService entregadorPostService;
    @Autowired
    private EntregadorV1PutService entregadorPutService;
    @Autowired
    private EntregadorV1GetService entregadorGetService;
    @Autowired
    private EntregadorV1PatchService entregadorPatchService;

    @Autowired
    private EntregadorV1DeleteService entregadorDeleteService;

    @PostMapping
    public ResponseEntity<Entregador> inserirEntregador(
        @RequestBody @Valid EntregadorPostPutRequestDTO EntregadorPostPutDTO
        ) throws Exception{
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.entregadorPostService.post(EntregadorPostPutDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Entregador> atualizarEntregador(
        @RequestBody @Valid EntregadorPostPutRequestDTO entregadorPostPutDTO,
        @Param("codigoAcesso") String codigoAcesso,
        @PathVariable Long id
        ) throws Exception {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.entregadorPutService.put(entregadorPostPutDTO, codigoAcesso, id));
    }

    @PatchMapping("/{id}/disponibilidade")
    public ResponseEntity<EntregadorResponseDTO> atualizarDisponibilidade(
            @RequestBody @Valid EntregadorPatchDto entregadorPatchDTO,
            @PathVariable Long id,
            @Param("codigoAcesso") String codigoAcesso
    ) throws Exception{
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.entregadorPatchService.atualizaDisponibilidade(entregadorPatchDTO, id, codigoAcesso));

    }


    @GetMapping("/{id}")
    public ResponseEntity<EntregadorResponseDTO> consultarEntregador(
        @PathVariable Long id
        ) throws  Exception{
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.entregadorGetService.getOne(id));
    }
    @GetMapping
    public ResponseEntity<List<EntregadorResponseDTO>> consultarFornecedores(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.entregadorGetService.getAll());

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @Param("codigoAcesso") String codigoAcesso,
            @PathVariable Long id
            ) throws Exception{
        this.entregadorDeleteService.delete(id,codigoAcesso);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
