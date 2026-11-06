package com.cesde.aguas_paraiso.service;

import com.cesde.aguas_paraiso.dto.TipoPredioRequestDTO;
import com.cesde.aguas_paraiso.dto.TipoPredioResponseDTO;
import com.cesde.aguas_paraiso.model.entity.TipoPredio;
import com.cesde.aguas_paraiso.repository.TipoPredioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar la lógica de negocio
 * relacionada con los tipos de predio de Aguas Paraíso.
 *
 * Este servicio utiliza DTO para separar la entidad JPA
 * TipoPredio del contrato público de la API.
 *
 * TipoPredioRequestDTO contiene los datos recibidos
 * para registrar o actualizar un tipo de predio.
 *
 * TipoPredioResponseDTO contiene la información que
 * se devuelve al controlador y posteriormente al cliente.
 *
 * Las conversiones se realizan dentro del servicio,
 * sin utilizar una clase Mapper independiente.
 */
@Service
@Transactional(readOnly = true)
public class TipoPredioService {

    /**
     * Repositorio que permite consultar y modificar
     * los tipos de predio almacenados en la base de datos.
     */
    private final TipoPredioRepository tipoPredioRepository;

    /**
     * Constructor utilizado por Spring para inyectar
     * automáticamente TipoPredioRepository.
     *
     * @param tipoPredioRepository repositorio de tipos de predio
     */
    public TipoPredioService(
            TipoPredioRepository tipoPredioRepository
    ) {
        this.tipoPredioRepository = tipoPredioRepository;
    }

    /**
     * Consulta todos los tipos de predio registrados.
     *
     * El repositorio devuelve entidades TipoPredio.
     * Cada entidad se transforma en TipoPredioResponseDTO
     * antes de salir de la capa de servicio.
     *
     * @return lista de tipos de predio representados como DTO
     */
    public List<TipoPredioResponseDTO> listarTodos() {
        return tipoPredioRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Busca un tipo de predio mediante su identificador.
     *
     * Si el registro existe, Optional.map ejecuta
     * convertirADTO y devuelve el DTO correspondiente.
     * Si no existe, el Optional permanece vacío.
     *
     * @param id identificador interno del tipo de predio
     * @return Optional con el DTO o vacío si no existe
     */
    public Optional<TipoPredioResponseDTO> buscarPorId(
            Long id
    ) {
        return tipoPredioRepository.findById(id)
                .map(this::convertirADTO);
    }

    /**
     * Busca un tipo de predio por su nombre exacto.
     *
     * La consulta ignora diferencias entre mayúsculas
     * y minúsculas gracias al método del repositorio.
     *
     * @param nombre nombre exacto que se desea buscar
     * @return Optional con el DTO o vacío si no existe
     */
    public Optional<TipoPredioResponseDTO> buscarPorNombre(
            String nombre
    ) {
        return tipoPredioRepository
                .findByNombreIgnoreCase(nombre)
                .map(this::convertirADTO);
    }

    /**
     * Busca tipos de predio cuyo nombre contenga
     * el texto recibido.
     *
     * La consulta es parcial e ignora diferencias
     * entre mayúsculas y minúsculas.
     *
     * @param nombre texto que debe aparecer en el nombre
     * @return lista de coincidencias convertidas a DTO
     */
    public List<TipoPredioResponseDTO> buscarPorNombreParcial(
            String nombre
    ) {
        return tipoPredioRepository
                .findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Registra un nuevo tipo de predio.
     *
     * REGLA DE NEGOCIO:
     * No puede existir otro tipo de predio con el mismo
     * nombre, incluso si cambia el uso de mayúsculas.
     *
     * El DTO de entrada se utiliza para construir
     * internamente la entidad TipoPredio.
     *
     * @param dto datos necesarios para registrar el tipo de predio
     * @return información del registro creado como ResponseDTO
     */
    @Transactional
    public TipoPredioResponseDTO registrar(
            TipoPredioRequestDTO dto
    ) {
        if (tipoPredioRepository.existsByNombreIgnoreCase(
                dto.nombre()
        )) {
            throw new IllegalArgumentException(
                    "Ya existe un tipo de predio registrado con este nombre"
            );
        }

        TipoPredio tipoPredio = TipoPredio.builder()
                .nombre(dto.nombre())
                .descripcion(dto.descripcion())
                .build();

        TipoPredio tipoPredioGuardado =
                tipoPredioRepository.save(tipoPredio);

        return convertirADTO(tipoPredioGuardado);
    }

    /**
     * Actualiza un tipo de predio existente.
     *
     * Primero comprueba que el ID exista.
     * Después valida que el nombre enviado no pertenezca
     * a otro registro diferente.
     *
     * @param id identificador del registro que se actualizará
     * @param dto nuevos datos del tipo de predio
     * @return información actualizada como ResponseDTO
     */
    @Transactional
    public TipoPredioResponseDTO actualizar(
            Long id,
            TipoPredioRequestDTO dto
    ) {
        TipoPredio tipoPredioExistente =
                tipoPredioRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "No existe un tipo de predio con el ID: "
                                                + id
                                )
                        );

        Optional<TipoPredio> tipoPredioConNombre =
                tipoPredioRepository.findByNombreIgnoreCase(
                        dto.nombre()
                );

        /*
         * Si existe un registro con el nombre solicitado,
         * se permite únicamente cuando corresponde al mismo ID
         * que se está actualizando.
         */
        if (tipoPredioConNombre.isPresent()
                && !tipoPredioConNombre.get()
                .getId()
                .equals(id)) {
            throw new IllegalArgumentException(
                    "El nombre ya pertenece a otro tipo de predio"
            );
        }

        tipoPredioExistente.setNombre(dto.nombre());
        tipoPredioExistente.setDescripcion(dto.descripcion());

        TipoPredio tipoPredioActualizado =
                tipoPredioRepository.save(tipoPredioExistente);

        return convertirADTO(tipoPredioActualizado);
    }

    /**
     * Elimina un tipo de predio mediante su ID.
     *
     * REGLA DE NEGOCIO:
     * Antes de eliminar, se verifica que el registro exista.
     *
     * @param id identificador del tipo de predio que se eliminará
     */
    @Transactional
    public void eliminar(Long id) {
        if (!tipoPredioRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "No existe un tipo de predio con el ID: " + id
            );
        }

        tipoPredioRepository.deleteById(id);
    }

    /**
     * Convierte una entidad TipoPredio en el DTO
     * utilizado por las respuestas de la API.
     *
     * La entidad permanece dentro de las capas internas,
     * mientras el controlador recibe un objeto preparado
     * específicamente para exponer información al cliente.
     *
     * @param tipoPredio entidad que se desea convertir
     * @return DTO con ID, nombre y descripción
     */
    public TipoPredioResponseDTO convertirADTO(
            TipoPredio tipoPredio
    ) {
        return new TipoPredioResponseDTO(
                tipoPredio.getId(),
                tipoPredio.getNombre(),
                tipoPredio.getDescripcion()
        );
    }
}
