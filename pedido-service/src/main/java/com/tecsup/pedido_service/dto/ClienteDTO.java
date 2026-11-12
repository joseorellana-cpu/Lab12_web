package com.tecsup.pedido_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datos del cliente que interesan al pedido (los demás campos que envía
 * cliente-service se ignoran al deserializar).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private Long id;
    private String nombre;
    private String apellido;
}
