package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.estabelecimento.EstabelecimentoPostPutRequestDTO;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoDeleteService;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoPostService;
import com.ufcg.psoft.commerce.service.estabelecimento.IEstabelecimentoPutService;
import com.ufcg.psoft.commerce.service.pedido.IPedidoGetService;
import com.ufcg.psoft.commerce.service.sabor.SaborCardapioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/estabelecimentos",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class EstabelecimentoV1RestController {

    @Autowired
    IEstabelecimentoPostService estabelecimentoPostService;

    @Autowired
    IEstabelecimentoDeleteService estabelecimentoDeleteService;

    @Autowired
    IEstabelecimentoPutService estabelecimentoPutService;

    @Autowired
    SaborCardapioService saborCardapioService;

    @Autowired
    IPedidoGetService pedidoGetService;

    @PostMapping
    public ResponseEntity<?> cadastrarEstabelecimento(
            @RequestBody @Valid EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(estabelecimentoPostService.cadastrar(estabelecimentoPostPutRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> apagarEstabelecimento(
            @PathVariable Long id,
            @RequestParam String codigoAcesso
    ) {
        estabelecimentoDeleteService.excluir(id, codigoAcesso);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarEstabelecimento(
            @PathVariable Long id,
            @RequestParam String codigoAcesso,
            @RequestBody @Valid EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(estabelecimentoPutService.atualizar(id, codigoAcesso, estabelecimentoPostPutRequestDTO));
    }

    @GetMapping("/{id}/sabores")
    public ResponseEntity<?> verSabores(
            @PathVariable Long id
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(saborCardapioService.buscarCardapio(id));
    }

    @GetMapping("/{id}/sabores/tipo")
    public ResponseEntity<?> verSaboresTipo(
            @PathVariable Long id,
            @RequestParam Character tipo
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(saborCardapioService.buscarCardapioTipo(id, tipo));
    }
    
    @GetMapping("/{estabelecimentoId}/pedidos")
    public ResponseEntity<?> verTodosPedidos(
            @PathVariable Long estabelecimentoId,
            @RequestParam @Valid String codigoAcesso
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoGetService.buscarPedidosEstabelecimento(estabelecimentoId, codigoAcesso));
    }

    @GetMapping("/{estabelecimentoId}/pedidos/{pedidoId}")
    public ResponseEntity<?> verPedido(
            @PathVariable Long estabelecimentoId,
            @PathVariable Long pedidoId,
            @RequestParam @Valid String codigoAcesso
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoGetService.buscarPedidoEstabelecimento(estabelecimentoId, codigoAcesso, pedidoId));
    }

}
