package com.tecsup.pedido_service.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Escribe los precios enteros sin decimales (2500.0 -> 2500)
 * y conserva los decimales cuando existen (2499.9 -> 2499.9).
 */
public class PrecioSerializer extends JsonSerializer<Double> {

    @Override
    public void serialize(Double valor, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (!valor.isInfinite() && valor == Math.rint(valor)) {
            gen.writeNumber(valor.longValue());
        } else {
            gen.writeNumber(valor);
        }
    }
}
