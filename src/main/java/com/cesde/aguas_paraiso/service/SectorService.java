package com.cesde.aguas_paraiso.service;

import com.cesde.aguas_paraiso.dto.SectorRequestDTO;
import com.cesde.aguas_paraiso.dto.SectorResponseDTO;
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

    public SectorService(
            SectorRepository sectorRepository
    ) {
        this.sectorRepository = sectorRepository;
    }

    /*
     * Consulta todos los sectores registrados
     * y convierte cada entidad en SectorResponseDTO.
     */
    public List<SectorResponseDTO> listarTodos() {
        return sectorRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /*
     * Busca un sector mediante su identificador interno.
     *
     * Si el sector existe, la entidad encontrada
     * se transforma en SectorResponseDTO.
     */
    public Optional<SectorResponseDTO> buscarPorId(
            Long id
    ) {
        return sectorRepository.findById(id)
                .map(this::convertirADTO);
    }

    /*
     * Busca un sector por su nombre exacto,
     * ignorando mayúsculas y minúsculas.
     */
    public Optional<SectorResponseDTO> buscarPorNombre(
            String nombre
    ) {
        return sectorRepository
                .findByNombreIgnoreCase(nombre)
                .map(this::convertirADTO);
    }

    /*
     * Busca los sectores cuyo nombre contenga
     * el texto recibido.
     */
    public List<SectorResponseDTO> buscarPorNombreParcial(
            String nombre
    ) {
        return sectorRepository
                .findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /*
     * Registra un sector utilizando los datos
     * recibidos mediante SectorRequestDTO.
     */
    @Transactional
    public SectorResponseDTO registrar(
            SectorRequestDTO dto
    ) {
        if (sectorRepository.existsByNombreIgnoreCase(
                dto.nombre()
        )) {
            throw new IllegalArgumentException(
                    "Ya existe un sector registrado con este nombre"
            );
        }

        Sector sector = Sector.builder()
                .nombre(dto.nombre())
                .descripcion(dto.descripcion())
                .build();

        Sector sectorGuardado =
                sectorRepository.save(sector);

        return convertirADTO(sectorGuardado);
    }

    /*
     * Actualiza un sector existente utilizando
     * los datos recibidos mediante SectorRequestDTO.
     */
    @Transactional
    public SectorResponseDTO actualizar(
            Long id,
            SectorRequestDTO dto
    ) {
        Sector sectorExistente = sectorRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe un sector con el ID: " + id
                        )
                );

        Optional<Sector> sectorConNombre =
                sectorRepository.findByNombreIgnoreCase(
                        dto.nombre()
                );

        /*
         * Si existe un sector con ese nombre,
         * verificamos que no sea un registro diferente.
         */
        if (sectorConNombre.isPresent()
                && !sectorConNombre.get()
                .getId()
                .equals(id)) {
            throw new IllegalArgumentException(
                    "El nombre ya pertenece a otro sector"
            );
        }

        sectorExistente.setNombre(dto.nombre());
        sectorExistente.setDescripcion(dto.descripcion());

        Sector sectorActualizado =
                sectorRepository.save(sectorExistente);

        return convertirADTO(sectorActualizado);
    }

    /*
     * Elimina un sector después de comprobar
     * que exista en la base de datos.
     */
    @Transactional
    public void eliminar(Long id) {
        if (!sectorRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "No existe un sector con el ID: " + id
            );
        }

        sectorRepository.deleteById(id);
    }

    /*
     * Convierte una entidad Sector en un
     * SectorResponseDTO.
     */
    public SectorResponseDTO convertirADTO(
            Sector sector
    ) {
        return new SectorResponseDTO(
                sector.getId(),
                sector.getNombre(),
                sector.getDescripcion()
        );
    }
}

