package com.tecsup.producto_service.model;

import com.tecsup.producto_service.dto.CategoriaDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Producto persistida en bd_producto_dev.
 * Solo guarda el identificador de la categoría (categoriaId): NO hay relación JPA
 * con otro microservicio. El objeto categoria se completa en tiempo de ejecución vía Feign.
 */
@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 150)
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    @Column(nullable = false)
    private Double precio;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private int stock;

    // Identificador de la categoría que vive en categoria-service
    @Column(name = "categoria_id")
    private Long categoriaId;

    // Dato remoto: no se persiste, se obtiene de categoria-service
    @Transient
    private CategoriaDTO categoria;
}
