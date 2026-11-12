package com.tecsup.producto_service.client;

import com.tecsup.producto_service.dto.CategoriaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente declarativo de categoria-service.
 * "name" es el nombre registrado en Eureka: no se usa host ni puerto fijo,
 * Spring Cloud LoadBalancer resuelve la instancia disponible.
 */
@FeignClient(name = "categoria-service")
public interface CategoriaClient {

    @GetMapping("/api/categorias/{id}")
    CategoriaDTO obtenerCategoria(@PathVariable("id") Long id);
}
