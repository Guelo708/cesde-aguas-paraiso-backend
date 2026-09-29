package com.cesde.aguas_paraiso.repository;

import com.cesde.aguas_paraiso.model.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectorRepository extends JpaRepository<Sector, Long> {

    Optional<Sector> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    List<Sector> findByNombreContainingIgnoreCase(String nombre);
}