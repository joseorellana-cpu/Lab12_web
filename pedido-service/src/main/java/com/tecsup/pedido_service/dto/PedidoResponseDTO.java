package com.tecsup.pedido_service.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.tecsup.pedido_service.model.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta "enriquecida" del pedido: combina datos locales (id, cantidad, estado)
 * con datos remotos obtenidos por Feign (cliente y producto).
 * Formato: {"id":1,"cantidad":2,"cliente":{...},"producto":{...},"estado":"PENDIENTE"}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "cantidad", "cliente", "producto", "estado"})
public class PedidoResponseDTO {
    private Long id;
    private int cantidad;
    private ClienteDTO cliente;
    private ProductoDTO producto;
    private EstadoPedido estado;
}
