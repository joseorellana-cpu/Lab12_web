package com.tecsup.pedido_service.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datos del producto que interesan al pedido (stock, categoría, etc. se ignoran).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String nombre;

    // Se serializa 2500.0 como 2500 para respetar el formato de respuesta pedido
    @JsonSerialize(using = PrecioSerializer.class)
    private Double precio;
}
