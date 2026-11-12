package com.tecsup.pedido_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Pedido persistida en bd_pedido_dev.
 * Guarda SOLO los identificadores de cliente y producto: los datos completos
 * se obtienen en tiempo de ejecución desde cliente-service y producto-service.
 */
@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El clienteId es obligatorio")
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @NotNull(message = "El productoId es obligatorio")
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Positive(message = "La cantidad debe ser mayor a cero")
    @Column(nullable = false)
    private int cantidad;

    // Se guarda como texto (ANULADO, PENDIENTE, ATENDIDO); por defecto PENDIENTE
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;
}
