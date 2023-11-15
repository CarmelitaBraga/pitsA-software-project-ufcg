package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.service.associacao.IAssociacaoPostService;
import com.ufcg.psoft.commerce.service.associacao.IAssociacaoPatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/associacoes",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class AssociacaoV1RestController {

    @Autowired
    IAssociacaoPostService associacaoPostService;

    @Autowired
    IAssociacaoPatchService associacaoPatchService;

    @PostMapping
    public ResponseEntity<?> cadastrarAssociacao(
            @RequestParam Long entregadorId,
            @RequestParam String codigoAcesso,
            @RequestParam Long estabelecimentoId
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(associacaoPostService.cadastrar(entregadorId, codigoAcesso, estabelecimentoId));
    }
    @PatchMapping ("/{associacaoId}/status")
    public ResponseEntity<?> atualizarAssociacao(
            @PathVariable Long associacaoId,
            @RequestParam Long estabelecimentoId,
            @RequestParam String codigoAcesso,
            @RequestParam Boolean status
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(associacaoPatchService.atualizarStatus(associacaoId, estabelecimentoId, codigoAcesso, status));
    }
}
