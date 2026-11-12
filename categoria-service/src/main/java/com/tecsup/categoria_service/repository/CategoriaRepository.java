package com.tecsup.categoria_service.repository;

import com.tecsup.categoria_service.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA de categorías (CRUD heredado de JpaRepository).
 */
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
