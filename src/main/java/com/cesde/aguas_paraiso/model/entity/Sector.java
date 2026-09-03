package com.cesde.aguas_paraiso.model.entity;

import com.cesde.aguas_paraiso.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    // Relación ManyToMany con Usuario
    @ManyToMany(mappedBy = "sectores")
    private List<Usuario> usuarios;
}

