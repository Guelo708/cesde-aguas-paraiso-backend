package com.cesde.aguas_paraiso.model.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Clase Base Heredable para todas las entidades JPA.
 * Evita duplicar el ID y las fechas de auditoría en cada entidad.
 */
@MappedSuperclass // 👈 Le dice a JPA que los campos de esta clase se heredan como columnas en las tablas hijas
@Getter
@Setter
public abstract class BaseEntity {

    // 1. Identificador Único Autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 2. Fecha de Creación Automática (Inmutable tras el INSERT)
    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // 3. Fecha de Última Actualización Automática (Se actualiza en cada UPDATE)
    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
