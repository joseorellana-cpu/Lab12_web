package com.tecsup.pedido_service.model;

/**
 * Estados posibles de un pedido. Un pedido nunca se elimina: solo cambia de estado.
 */
public enum EstadoPedido {
    ANULADO,
    PENDIENTE,
    ATENDIDO
}
