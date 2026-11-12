package com.tecsup.producto_service.services;

import com.tecsup.producto_service.client.CategoriaClient;
import com.tecsup.producto_service.dto.CategoriaDTO;
import com.tecsup.producto_service.exception.RecursoNoEncontradoException;
import com.tecsup.producto_service.model.Producto;
import com.tecsup.producto_service.repository.ProductoRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lógica de negocio de productos. La categoría se obtiene SIEMPRE por REST
 * (OpenFeign) desde categoria-service; nunca se consulta su base de datos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaClient categoriaClient;

    /**
     * Lista los productos y completa la categoría de cada uno.
     * Se usa un pequeño caché local para no repetir llamadas por la misma categoría.
     */
    public List<Producto> listar() {
        List<Producto> productos = productoRepository.findAll();
        Map<Long, CategoriaDTO> cache = new HashMap<>();
        productos.forEach(p -> {
            if (p.getCategoriaId() != null) {
                p.setCategoria(cache.computeIfAbsent(p.getCategoriaId(), this::buscarCategoriaTolerante));
            }
        });
        return productos;
    }

    /**
     * Registra un producto. Si trae categoriaId, primero valida que la categoría
     * exista en categoria-service (si no existe responde 404 y no se guarda).
     */
    public Producto guardar(Producto producto) {
        producto.setId(null);
        CategoriaDTO categoria = null;
        if (producto.getCategoriaId() != null) {
            categoria = buscarCategoriaObligatoria(producto.getCategoriaId());
        }
        Producto guardado = productoRepository.save(producto);
        guardado.setCategoria(categoria);
        return guardado;
    }

    /**
     * Obtiene un producto por id y completa su categoría.
     */
    public Producto obtener(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto con id " + id + " no encontrado"));
        if (producto.getCategoriaId() != null) {
            producto.setCategoria(buscarCategoriaTolerante(producto.getCategoriaId()));
        }
        return producto;
    }

    /**
     * Actualiza los datos de un producto existente.
     */
    public Producto actualizar(Long id, Producto datos) {
        Producto actual = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto con id " + id + " no encontrado"));
        CategoriaDTO categoria = null;
        if (datos.getCategoriaId() != null) {
            categoria = buscarCategoriaObligatoria(datos.getCategoriaId());
        }
        actual.setNombre(datos.getNombre());
        actual.setPrecio(datos.getPrecio());
        actual.setStock(datos.getStock());
        actual.setCategoriaId(datos.getCategoriaId());
        Producto guardado = productoRepository.save(actual);
        guardado.setCategoria(categoria);
        return guardado;
    }

    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Producto con id " + id + " no encontrado");
        }
        productoRepository.deleteById(id);
    }

    // La categoría DEBE existir: un 404 remoto se convierte en 404 local con mensaje claro
    private CategoriaDTO buscarCategoriaObligatoria(Long categoriaId) {
        try {
            return categoriaClient.obtenerCategoria(categoriaId);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException(
                    "La categoría con id " + categoriaId + " no existe en categoria-service");
        }
    }

    // Para consultas: si la categoría fue eliminada se devuelve null en lugar de fallar.
    // Si categoria-service está caído, el error se propaga (503 en el manejador global).
    private CategoriaDTO buscarCategoriaTolerante(Long categoriaId) {
        try {
            return categoriaClient.obtenerCategoria(categoriaId);
        } catch (FeignException.NotFound e) {
            log.warn("Categoría {} no encontrada en categoria-service", categoriaId);
            return null;
        }
    }
}
