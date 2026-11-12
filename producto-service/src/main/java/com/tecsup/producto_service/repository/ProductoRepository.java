package com.tecsup.producto_service.repository;

import com.tecsup.producto_service.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA de productos.
 */
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
