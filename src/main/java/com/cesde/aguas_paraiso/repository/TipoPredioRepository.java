package com.cesde.aguas_paraiso.repository;

import com.cesde.aguas_paraiso.model.entity.TipoPredio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TipoPredioRepository
        extends JpaRepository<TipoPredio, Long> {

    Optional<TipoPredio> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    List<TipoPredio> findByNombreContainingIgnoreCase(String nombre);
}