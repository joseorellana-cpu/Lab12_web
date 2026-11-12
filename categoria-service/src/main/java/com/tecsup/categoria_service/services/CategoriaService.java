package com.tecsup.categoria_service.services;

import com.tecsup.categoria_service.exception.RecursoNoEncontradoException;
import com.tecsup.categoria_service.model.Categoria;
import com.tecsup.categoria_service.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica de negocio de categorías.
 */
@Service
@RequiredArgsConstructor
public class CategoriaService {

    private static final String ESTADO_POR_DEFECTO = "ACTIVO";

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Categoria obtener(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría con id " + id + " no encontrada"));
    }

    @Transactional
    public Categoria guardar(Categoria categoria) {
        categoria.setId(null); // se fuerza la creación de un registro nuevo
        if (categoria.getEstado() == null) {
            categoria.setEstado(ESTADO_POR_DEFECTO);
        }
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria actualizar(Long id, Categoria datos) {
        Categoria actual = obtener(id);
        actual.setNombre(datos.getNombre());
        if (datos.getEstado() != null) {
            actual.setEstado(datos.getEstado());
        }
        return categoriaRepository.save(actual);
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria actual = obtener(id); // lanza 404 si no existe
        categoriaRepository.delete(actual);
    }
}
