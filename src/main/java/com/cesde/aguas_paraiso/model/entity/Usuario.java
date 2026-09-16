package com.cesde.aguas_paraiso.model.entity;

import com.cesde.aguas_paraiso.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario extends BaseEntity {

    @Column(nullable = false, unique = true, length = 15)
    private String cedula;

    @Column(nullable = false, length = 50)
    private String primerNombre;

    @Column(length = 50)
    private String segundoNombre;

    @Column(nullable = false, length = 50)
    private String primerApellido;

    @Column(length = 50)
    private String segundoApellido;

    @Column(length = 20)
    private String celular;

    @OneToMany(mappedBy = "propietario")
    private List<Predio> predios;
}


