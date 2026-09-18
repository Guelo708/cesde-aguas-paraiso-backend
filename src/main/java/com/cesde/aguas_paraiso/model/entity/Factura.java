package com.cesde.aguas_paraiso.model.entity;

import com.cesde.aguas_paraiso.model.base.BaseEntity;
import com.cesde.aguas_paraiso.model.enums.EstadoFactura;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "facturas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factura extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "predio_id", nullable = false)
    private Predio predio;

    @ManyToOne
    @JoinColumn(name = "tarifa_id", nullable = false)
    private Tarifa tarifaAplicada;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorFactura;

    @Column(nullable = false)
    private LocalDate fechaGeneracion;
D
    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFactura estado;
}