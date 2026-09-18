package com.cesde.aguas_paraiso.model.entity;

import com.cesde.aguas_paraiso.model.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "sectores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sector extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String descripcion;
}
