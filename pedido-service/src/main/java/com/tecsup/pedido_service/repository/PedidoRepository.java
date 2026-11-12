package com.tecsup.pedido_service.repository;

import com.tecsup.pedido_service.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA de pedidos.
 */
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
