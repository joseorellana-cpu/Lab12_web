package com.tecsup.pedido_service.services;

import com.tecsup.pedido_service.client.ClienteClient;
import com.tecsup.pedido_service.client.ProductoClient;
import com.tecsup.pedido_service.dto.ClienteDTO;
import com.tecsup.pedido_service.dto.PedidoResponseDTO;
import com.tecsup.pedido_service.dto.ProductoDTO;
import com.tecsup.pedido_service.exception.RecursoNoEncontradoException;
import com.tecsup.pedido_service.model.EstadoPedido;
import com.tecsup.pedido_service.model.Pedido;
import com.tecsup.pedido_service.repository.PedidoRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lógica de negocio de pedidos.
 * Los datos de cliente y producto se obtienen SOLO por REST (OpenFeign + Eureka).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteClient clienteClient;
    private final ProductoClient productoClient;

    /**
     * Registra un pedido: antes de guardar valida vía Feign que el cliente
     * y el producto existan. El estado inicial siempre es PENDIENTE.
     */
    public PedidoResponseDTO registrar(Pedido pedido) {
        ClienteDTO cliente = buscarClienteObligatorio(pedido.getClienteId());
        ProductoDTO producto = buscarProductoObligatorio(pedido.getProductoId());

        pedido.setId(null);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        Pedido guardado = pedidoRepository.save(pedido);

        return construirRespuesta(guardado, cliente, producto);
    }

    /**
     * Lista todos los pedidos con sus datos de cliente y producto.
     * Se usan cachés locales para no repetir llamadas remotas por el mismo id.
     */
    public List<PedidoResponseDTO> listar() {
        Map<Long, ClienteDTO> clientes = new HashMap<>();
        Map<Long, ProductoDTO> productos = new HashMap<>();
        return pedidoRepository.findAll().stream()
                .map(p -> construirRespuesta(p,
                        clientes.computeIfAbsent(p.getClienteId(), this::buscarClienteTolerante),
                        productos.computeIfAbsent(p.getProductoId(), this::buscarProductoTolerante)))
                .toList();
    }

    /**
     * Obtiene un pedido: lo busca en su propia BD, luego consulta cliente y
     * producto por Feign y arma el PedidoResponseDTO completo.
     */
    public PedidoResponseDTO obtener(Long id) {
        Pedido pedido = buscarPedido(id);
        ClienteDTO cliente = buscarClienteTolerante(pedido.getClienteId());
        ProductoDTO producto = buscarProductoTolerante(pedido.getProductoId());
        return construirRespuesta(pedido, cliente, producto);
    }

    /**
     * Cambia el estado del pedido (no existe eliminación de pedidos).
     */
    public PedidoResponseDTO cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = buscarPedido(id);
        pedido.setEstado(nuevoEstado);
        Pedido actualizado = pedidoRepository.save(pedido);
        return construirRespuesta(actualizado,
                buscarClienteTolerante(actualizado.getClienteId()),
                buscarProductoTolerante(actualizado.getProductoId()));
    }

    // ----------------- métodos auxiliares -----------------

    private Pedido buscarPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido con id " + id + " no encontrado"));
    }

    private PedidoResponseDTO construirRespuesta(Pedido pedido, ClienteDTO cliente, ProductoDTO producto) {
        return PedidoResponseDTO.builder()
                .id(pedido.getId())
                .cantidad(pedido.getCantidad())
                .cliente(cliente)
                .producto(producto)
                .estado(pedido.getEstado())
                .build();
    }

    // Al registrar, el cliente DEBE existir (404 remoto -> 404 local con mensaje claro)
    private ClienteDTO buscarClienteObligatorio(Long clienteId) {
        try {
            return clienteClient.obtenerCliente(clienteId);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("El cliente con id " + clienteId + " no existe en cliente-service");
        }
    }

    // Al registrar, el producto DEBE existir
    private ProductoDTO buscarProductoObligatorio(Long productoId) {
        try {
            return productoClient.obtenerProducto(productoId);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("El producto con id " + productoId + " no existe en producto-service");
        }
    }

    // En consultas: si el cliente fue eliminado luego, se devuelve null en vez de fallar.
    // Si el servicio está caído, el error se propaga y el manejador global responde 503.
    private ClienteDTO buscarClienteTolerante(Long clienteId) {
        try {
            return clienteClient.obtenerCliente(clienteId);
        } catch (FeignException.NotFound e) {
            log.warn("Cliente {} ya no existe en cliente-service", clienteId);
            return null;
        }
    }

    private ProductoDTO buscarProductoTolerante(Long productoId) {
        try {
            return productoClient.obtenerProducto(productoId);
        } catch (FeignException.NotFound e) {
            log.warn("Producto {} ya no existe en producto-service", productoId);
            return null;
        }
    }
}
