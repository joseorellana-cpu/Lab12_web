package com.tecsup.cliente_service.repository;

import com.tecsup.cliente_service.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA de clientes.
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Verifica si ya existe otro cliente con el mismo DNI
    boolean existsByNroDni(String nroDni);

    boolean existsByNroDniAndIdNot(String nroDni, Long id);
}
