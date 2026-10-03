package com.cesde.aguas_paraiso.repository;

import com.cesde.aguas_paraiso.model.entity.Predio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PredioRepository extends JpaRepository<Predio, Long> {

    Optional<Predio> findByCodigoPredio(String codigoPredio);

    boolean existsByCodigoPredio(String codigoPredio);

    List<Predio> findByPropietarioId(Long usuarioId);

    List<Predio> findBySectorId(Long sectorId);

    List<Predio> findByTipoPredioId(Long tipoPredioId);
}
