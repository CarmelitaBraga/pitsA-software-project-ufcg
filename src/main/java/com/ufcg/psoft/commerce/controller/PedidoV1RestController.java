package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.pedido.PedidoPatchRequestDTO;
import com.ufcg.psoft.commerce.dto.pedido.PedidoPostPutRequestDTO;
import com.ufcg.psoft.commerce.service.pedido.IPedidoPatchPedidoEntregueService;
import com.ufcg.psoft.commerce.service.pedido.IPedidoPatchService;
import com.ufcg.psoft.commerce.service.pedido.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/pedidos",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class PedidoV1RestController {
    @Autowired
    private IPedidoPostService pedidoPostService;
    @Autowired
    private IPedidoPutService pedidoPutService;

    @Autowired
    private IPedidoGetService pedidoGetService;

    @Autowired
    private IPedidoDeleteService pedidoDeleteService;

    @Autowired
    private IPedidoPatchService pedidoPatchService;

    @Autowired
    private IPedidoPatchPedidoEntregueService pedidoPatchPedidoEntregueService;
    @Autowired
    private IPedidoPatchProntoService pedidoPatchProntoService;

    @Autowired
    private IPedidoPatchAtribuirEntregadorService pedidoPatchAtribuirEntregadorService;

    @PostMapping
    public ResponseEntity<?> cadastrarPedido(
            @RequestParam("clienteId") Long clienteId,
            @RequestParam("codigoAcesso") @Valid String codigoAcesso,
            @RequestParam("estabelecimentoId") Long estabelecimentoId,
            @RequestBody @Valid PedidoPostPutRequestDTO pedidoPostPutDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.pedidoPostService.cadastraPedido(clienteId, codigoAcesso, estabelecimentoId, pedidoPostPutDTO));
    }

    @PatchMapping
    public ResponseEntity<?> confirmarPagamento(
            @RequestParam("clienteId") Long clienteId,
            @RequestParam("codigoAcesso") String codigoAcesso,
            @RequestParam("pedidoId") Long pedidoID,
            @RequestBody @Valid PedidoPatchRequestDTO pedidoPatchRequestDTO
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.pedidoPatchService.confirmarPagamento(clienteId, codigoAcesso, pedidoID, pedidoPatchRequestDTO));
    }

    @PatchMapping("/{id}/status-pronto")
    public ResponseEntity<?> terminoPreparo(
            @PathVariable("id") Long pedidoId,
            @RequestParam("estabelecimentoId") Long estabelecimentoId,
            @RequestParam("codigoAcesso") String codigoAcesso
    ){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoPatchProntoService.disparaPronto(estabelecimentoId, codigoAcesso, pedidoId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPedido(
            @PathVariable("id") Long pedidoId,
            @RequestParam Long clienteId,
            @RequestParam @Valid String codigoAcesso,
            @RequestBody PedidoPostPutRequestDTO pedidoPostPutRequestDTO
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoPutService.atualizaPedido(pedidoId, clienteId, codigoAcesso, pedidoPostPutRequestDTO));
    }

    @GetMapping
    public ResponseEntity<?> obterPedidosCliente(
            @RequestParam Long clienteId,
            @RequestParam String codigoAcesso
    )
    {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoGetService.obterPedidosCliente(clienteId, codigoAcesso));
    }

    @DeleteMapping("/{id}/cliente")
    public ResponseEntity<?> cancelarPedidoCliente(
            @PathVariable Long id,
            @RequestParam("clienteId") Long clienteId,
            @RequestParam("codigoAcesso") String codigoAcesso
        ) {
        this.pedidoDeleteService.cancelarPedidoCliente(id, clienteId, codigoAcesso);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @DeleteMapping("/{id}/estabelecimento")
    public ResponseEntity<?> apagarPedidoEstabelecimento(
            @PathVariable Long id,
            @RequestParam("estabelecimentoId") Long estabelecimentoId,
            @RequestParam("codigoAcesso") String codigoAcesso
        ) {
        this.pedidoDeleteService.apagarPedidoEstabelecimento(id, estabelecimentoId, codigoAcesso);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPedido(
        @PathVariable Long id,
        @Param("clienteId") Long clienteId,
        @Param("codigoAcesso") String codigoAcesso
        ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoGetService.obterPedidoCliente(id, clienteId, codigoAcesso));
    }

    @GetMapping("/status")
    public ResponseEntity<?> buscarPedidosRecebidos(
            @Param("clienteId") Long clienteId,
            @Param("codigoAcesso") String codigoAcesso,
            @Param("status") String status
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoGetService.buscarPedidosClienteFiltradosPorStatus(status, clienteId, codigoAcesso));
    }

    @PatchMapping("/{pedidoId}/entrega")
    public ResponseEntity<?> confirmarRecebimentoPedido(
            @RequestParam("clienteId") Long clienteId,
            @RequestParam("codigoAcesso") String codigoAcesso,
            @PathVariable("pedidoId") Long pedidoID
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.pedidoPatchPedidoEntregueService.clienteRecebePedido(clienteId, codigoAcesso, pedidoID));
    }

    @PatchMapping("/{pedidoId}/atribuir-entregador")
    public ResponseEntity<?> atribuirEntregadorPedido(
            @RequestParam("estabelecimentoId") Long estabelecimentoId,
            @RequestParam("codigoAcesso") String codigoAcesso,
            @PathVariable("pedidoId") Long pedidoId,
            @RequestParam("entregadorId") Long entregadorId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.pedidoPatchAtribuirEntregadorService.atribuirEntregador(estabelecimentoId, codigoAcesso, pedidoId, entregadorId));
    }
}
