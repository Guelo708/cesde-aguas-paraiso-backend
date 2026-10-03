package com.cesde.aguas_paraiso.service;

import com.cesde.aguas_paraiso.model.entity.Sector;
import com.cesde.aguas_paraiso.repository.SectorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SectorService {

    private final SectorRepository sectorRepository;

    public SectorService(SectorRepository sectorRepository) {
        this.sectorRepository = sectorRepository;
    }

    public List<Sector> listarTodos() {
        return sectorRepository.findAll();
    }

    public Optional<Sector> buscarPorId(Long id) {
        return sectorRepository.findById(id);
    }

    public Optional<Sector> buscarPorNombre(String nombre) {
        return sectorRepository.findByNombreIgnoreCase(nombre);
    }

    public List<Sector> buscarPorNombreParcial(String nombre) {
        return sectorRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional
    public Sector registrar(Sector sector) {

        if (sectorRepository.existsByNombreIgnoreCase(
                sector.getNombre())) {
            throw new IllegalArgumentException(
                    "Ya existe un sector registrado con este nombre");
        }

        return sectorRepository.save(sector);
    }

    @Transactional
    public Sector actualizar(
            Long id,
            Sector datosActualizados) {

        Sector sectorExistente = sectorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un sector con el ID: " + id));

        Optional<Sector> sectorConNombre = sectorRepository.findByNombreIgnoreCase(
                datosActualizados.getNombre());

        if (sectorConNombre.isPresent()
                && !sectorConNombre.get().getId().equals(id)) {

            throw new IllegalArgumentException(
                    "El nombre ya pertenece a otro sector");
        }

        sectorExistente.setNombre(
                datosActualizados.getNombre());

        sectorExistente.setDescripcion(
                datosActualizados.getDescripcion());

        return sectorRepository.save(sectorExistente);
    }

    @Transactional
    public void eliminar(Long id) {

        if (!sectorRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "No existe un sector con el ID: " + id);
        }

        sectorRepository.deleteById(id);
    }
}