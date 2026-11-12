package com.tecsup.pedido_service.controller;

import com.tecsup.pedido_service.dto.PedidoResponseDTO;
import com.tecsup.pedido_service.model.EstadoPedido;
import com.tecsup.pedido_service.model.Pedido;
import com.tecsup.pedido_service.services.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * API REST de pedidos: /api/pedidos
 * No existe DELETE: un pedido solo cambia de estado (anular / pendiente / atender).
 */
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    // POST /api/pedidos -> 201 Created / 400 / 404 si cliente o producto no existen
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> registrar(@Valid @RequestBody Pedido pedido) {
        PedidoResponseDTO creado = pedidoService.registrar(pedido);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    // GET /api/pedidos -> 200 OK
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listar() {
        return ResponseEntity.ok(pedidoService.listar());
    }

    // GET /api/pedidos/{id} -> 200 OK / 404
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtener(id));
    }

    // PUT /api/pedidos/{id}/anular -> estado ANULADO
    @PutMapping("/{id}/anular")
    public ResponseEntity<PedidoResponseDTO> anular(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, EstadoPedido.ANULADO));
    }

    // PUT /api/pedidos/{id}/pendiente -> estado PENDIENTE
    @PutMapping("/{id}/pendiente")
    public ResponseEntity<PedidoResponseDTO> pendiente(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, EstadoPedido.PENDIENTE));
    }

    // PUT /api/pedidos/{id}/atender -> estado ATENDIDO
    @PutMapping("/{id}/atender")
    public ResponseEntity<PedidoResponseDTO> atender(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, EstadoPedido.ATENDIDO));
    }
}
