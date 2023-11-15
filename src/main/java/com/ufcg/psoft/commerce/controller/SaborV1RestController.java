package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.sabor.SaborPatchStatusDTO;
import com.ufcg.psoft.commerce.dto.sabor.SaborPostPutRequestDTO;
import com.ufcg.psoft.commerce.service.sabor.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/sabores",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class SaborV1RestController {
    @Autowired
    private SaborV1PostService saborV1PostService;
    @Autowired
    private SaborV1GetService saborV1GetService;
    @Autowired
    private SaborV1PutService saborV1PutService;
    @Autowired
    private SaborV1DeleteService saborV1DeleteService;
    @Autowired
    private SaborV1PatchStatusService saborV1PatchStatusService;

    @PostMapping
    public ResponseEntity<?> cadastrarSabor(
            @RequestParam(name = "estabelecimentoId") Long id,
            @RequestParam(name = "estabelecimentoCodigoAcesso") String codigoAcesso,
            @RequestBody @Valid SaborPostPutRequestDTO saborPostPutDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.saborV1PostService.cadastrarSabor(id, codigoAcesso, saborPostPutDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarSabor(
            @PathVariable("id") Long saborId,
            @RequestParam(name = "estabelecimentoId") Long estabelecimentoId,
            @RequestParam(name = "estabelecimentoCodigoAcesso") String estabelecimentoCodigoAcesso,
            @RequestBody @Valid SaborPostPutRequestDTO saborPostPutDto
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.saborV1PutService.atualizarSabor(saborId, estabelecimentoId, estabelecimentoCodigoAcesso, saborPostPutDto));
    }
    @PatchMapping("{id}/status")
    public ResponseEntity<?> atualizarSaborStatus(
            @PathVariable("id") Long saborId,
            @RequestParam(name = "estabelecimentoId") Long estabelecimentoId,
            @RequestParam(name = "estabelecimentoCodigoAcesso") String estabelecimentoCodigoAcesso,
            @RequestBody @Valid SaborPatchStatusDTO saborPatchStatusDTO
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.saborV1PatchStatusService.atualizarSaborStatus(saborId, estabelecimentoId, estabelecimentoCodigoAcesso, saborPatchStatusDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> apagarSabor(
            @PathVariable("id") Long saborId,
            @RequestParam(name = "estabelecimentoId") Long estabelecimentoId,
            @RequestParam(name = "estabelecimentoCodigoAcesso") String codigoAcesso
    ) {
        this.saborV1DeleteService.apagarSabor(saborId, estabelecimentoId, codigoAcesso);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarSabor(
            @PathVariable("id") Long saborId,
            @RequestParam(name = "estabelecimentoId") Long estabelecimentoId,
            @RequestParam(name = "estabelecimentoCodigoAcesso") String codigoAcesso
            ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.saborV1GetService.buscarUmSabor(saborId, estabelecimentoId, codigoAcesso));
    }

    @GetMapping
    public ResponseEntity<?> buscarTodosSabores(
            @RequestParam(name = "estabelecimentoId") Long estabelecimentoId,
            @RequestParam(name = "estabelecimentoCodigoAcesso") String codigoAcesso
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.saborV1GetService.buscarTodosSabores(estabelecimentoId, codigoAcesso));
    }
}