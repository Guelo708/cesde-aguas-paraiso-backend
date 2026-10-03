package com.cesde.aguas_paraiso.repository;

import com.cesde.aguas_paraiso.model.entity.Factura;
import com.cesde.aguas_paraiso.model.enums.EstadoFactura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FacturaRepository
        extends JpaRepository<Factura, Long> {

    Optional<Factura> findByPredioIdAndMesAndAnio(
            Long predioId,
            Integer mes,
            Integer anio
    );

    boolean existsByPredioIdAndMesAndAnio(
            Long predioId,
            Integer mes,
            Integer anio
    );

    List<Factura> findByPredioIdOrderByAnioDescMesDesc(
            Long predioId
    );

    List<Factura>
    findByPredioPropietarioIdOrderByAnioDescMesDesc(
            Long usuarioId
    );

    List<Factura> findByCedulaPropietarioOrderByAnioDescMesDesc(
            String cedulaPropietario
    );

    List<Factura>
    findByCedulaPropietarioAndEstadoOrderByAnioDescMesDesc(
            String cedulaPropietario,
            EstadoFactura estado
    );

    List<Factura> findByPredioPropietarioIdAndEstado(
            Long usuarioId,
            EstadoFactura estado
    );

    long countByPredioPropietarioIdAndEstado(
            Long usuarioId,
            EstadoFactura estado
    );

    long countByCedulaPropietarioAndEstado(
            String cedulaPropietario,
            EstadoFactura estado
    );

    List<Factura> findByFechaVencimientoBeforeAndEstadoNot(
            LocalDate fecha,
            EstadoFactura estado
    );

    List<Factura>
findByCedulaPropietarioAndFechaGeneracionBetweenOrderByFechaGeneracionDesc(
        String cedulaPropietario,
        LocalDate fechaInicial,
        LocalDate fechaFinal
);

List<Factura>
findByCedulaPropietarioAndEstadoAndFechaGeneracionBetweenOrderByFechaGeneracionDesc(
        String cedulaPropietario,
        EstadoFactura estado,
        LocalDate fechaInicial,
        LocalDate fechaFinal
);
}
