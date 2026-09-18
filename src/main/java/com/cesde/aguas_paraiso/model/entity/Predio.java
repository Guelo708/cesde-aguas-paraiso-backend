package com.cesde.aguas_paraiso.model.entity;

import com.cesde.aguas_paraiso.model.base.BaseEntity;
import com.cesde.aguas_paraiso.model.embeddable.Ubicacion;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "predios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Predio extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String codigoPredio;

    @Embedded
    private Ubicacion ubicacion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario propietario;

    @ManyToOne
    @JoinColumn(name = "sector_id", nullable = false)
    private Sector sector;

    @ManyToOne
    @JoinColumn(name = "tipo_predio_id", nullable = false)
    private TipoPredio tipoPredio;
}
