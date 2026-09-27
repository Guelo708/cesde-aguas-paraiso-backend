package com.cesde.aguas_paraiso.model.entity;

import com.cesde.aguas_paraiso.model.base.BaseEntity;
import com.cesde.aguas_paraiso.model.enums.EstadoFactura;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "facturas",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_factura_predio_periodo",
        columnNames = {"predio_id", "mes", "anio"}
    )
)
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

    @OneToMany(mappedBy = "factura")
    @Builder.Default
    private List<Pago> pagos = new ArrayList<>();

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorFactura;

    @Column(nullable = false)
    private LocalDate fechaGeneracion;

    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoFactura estado;
}