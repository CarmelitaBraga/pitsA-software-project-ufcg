package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.cliente.ClienteInteresseRequestDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.service.cliente.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/clientes",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ClienteV1RestController {

    @Autowired
    IClientePostService clientePostService;

    @Autowired
    IClientePutService clientePutService;

    @Autowired
    IClienteGetService clienteGetService;

    @Autowired
    IClienteDeleteService clienteDeleteService;

    @Autowired
    IClientePatchInteresseService clientePatchInteresseService;
    @PostMapping
    public ResponseEntity<?> cadastrarCliente(
        @RequestBody @Valid ClientePostPutRequestDTO clientePostPutRequestDTO
        ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.clientePostService.cadastraCliente(clientePostPutRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCliente(
            @PathVariable Long id,
            @RequestBody @Valid ClientePostPutRequestDTO clientePostPutRequestDTO,
            @RequestParam String codigoAcesso
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.clientePutService.atualizaCliente(id, codigoAcesso, clientePostPutRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> apagarCliente(
        @PathVariable Long id,
        @RequestParam String codigoAcesso
        ) {
        this.clienteDeleteService.deletaCliente(id, codigoAcesso);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarCliente(
        @PathVariable Long id
        ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.clienteGetService.retornaCliente(id));
    }

    @GetMapping
    public ResponseEntity<?> buscarTodos() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.clienteGetService.retornaTodos());
    }

    @PatchMapping
    public ResponseEntity<?> addSaborInteresse(
            @RequestBody @Valid ClienteInteresseRequestDTO clienteInteresseRequestDTO
            ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.clientePatchInteresseService.addInteresse(clienteInteresseRequestDTO));
    }
}
