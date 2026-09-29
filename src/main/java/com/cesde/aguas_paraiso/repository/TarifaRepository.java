package com.cesde.aguas_paraiso.repository;

import com.cesde.aguas_paraiso.model.entity.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TarifaRepository extends JpaRepository<Tarifa, Long> {

    List<Tarifa> findByTipoPredioIdOrderByFechaInicioDesc(
            Long tipoPredioId
    );

    @Query("""
        SELECT t
        FROM Tarifa t
        WHERE t.tipoPredio.id = :tipoPredioId
          AND t.fechaInicio <= :fecha
          AND (t.fechaFin IS NULL OR t.fechaFin >= :fecha)
    """)
    Optional<Tarifa> buscarTarifaVigente(
            @Param("tipoPredioId") Long tipoPredioId,
            @Param("fecha") LocalDate fecha
    );
}