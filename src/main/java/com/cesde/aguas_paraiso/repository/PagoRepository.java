package com.cesde.aguas_paraiso.repository;

import com.cesde.aguas_paraiso.model.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByFacturaIdOrderByFechaPagoDesc(
            Long facturaId
    );

    List<Pago>
    findByFacturaPredioPropietarioIdOrderByFechaPagoDesc(
            Long usuarioId
    );

    List<Pago>
    findByFacturaCedulaPropietarioOrderByFechaPagoDesc(
            String cedulaPropietario
    );

    List<Pago>
    findByFacturaCedulaPropietarioAndFechaPagoBetweenOrderByFechaPagoDesc(
            String cedulaPropietario,
            LocalDate fechaInicial,
            LocalDate fechaFinal
    );

    @Query("""
        SELECT COALESCE(SUM(p.valorPagado), 0)
        FROM Pago p
        WHERE p.factura.id = :facturaId
    """)
    BigDecimal sumarPagosPorFactura(
            @Param("facturaId") Long facturaId
    );

    @Query("""
        SELECT COALESCE(SUM(p.valorPagado), 0)
        FROM Pago p
        WHERE p.factura.predio.propietario.id = :usuarioId
    """)
    BigDecimal sumarPagosPorUsuario(
            @Param("usuarioId") Long usuarioId
    );

    @Query("""
        SELECT COALESCE(SUM(p.valorPagado), 0)
        FROM Pago p
        WHERE p.factura.cedulaPropietario = :cedula
    """)
    BigDecimal sumarPagosPorCedula(
            @Param("cedula") String cedula
    );
}
