package com.cesde.aguas_paraiso.service;

import com.cesde.aguas_paraiso.dto.TarifaRequestDTO;
import com.cesde.aguas_paraiso.dto.TarifaResponseDTO;
import com.cesde.aguas_paraiso.dto.TipoPredioResponseDTO;
import com.cesde.aguas_paraiso.model.entity.Tarifa;
import com.cesde.aguas_paraiso.model.entity.TipoPredio;
import com.cesde.aguas_paraiso.repository.TarifaRepository;
import com.cesde.aguas_paraiso.repository.TipoPredioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar la lógica de negocio
 * relacionada con las tarifas de Aguas Paraíso.
 *
 * Este servicio utiliza DTO para separar la entidad JPA Tarifa
 * del contrato público que ofrece la API.
 *
 * TarifaRequestDTO contiene los datos recibidos para registrar
 * o actualizar una tarifa:
 *
 * - ID del tipo de predio.
 * - Valor de la tarifa.
 * - Fecha de inicio.
 * - Fecha final opcional.
 *
 * TarifaResponseDTO contiene la información que se devuelve
 * al controlador, incluyendo el TipoPredioResponseDTO asociado.
 *
 * Las conversiones se realizan dentro del servicio,
 * sin utilizar una clase Mapper independiente.
 */
@Service
@Transactional(readOnly = true)
public class TarifaService {

    /**
     * Repositorio utilizado para consultar y modificar tarifas.
     */
    private final TarifaRepository tarifaRepository;

    /**
     * Repositorio utilizado para comprobar que el tipo de predio
     * recibido mediante tipoPredioId realmente exista.
     */
    private final TipoPredioRepository tipoPredioRepository;

    /**
     * Constructor utilizado por Spring para inyectar
     * automáticamente los repositorios requeridos.
     *
     * @param tarifaRepository repositorio de tarifas
     * @param tipoPredioRepository repositorio de tipos de predio
     */
    public TarifaService(
            TarifaRepository tarifaRepository,
            TipoPredioRepository tipoPredioRepository
    ) {
        this.tarifaRepository = tarifaRepository;
        this.tipoPredioRepository = tipoPredioRepository;
    }

    /**
     * Consulta todas las tarifas registradas.
     *
     * El repositorio devuelve entidades Tarifa.
     * Cada entidad se convierte en TarifaResponseDTO
     * antes de salir de la capa de servicio.
     *
     * @return lista de tarifas convertidas a DTO
     */
    public List<TarifaResponseDTO> listarTodas() {
        return tarifaRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Busca una tarifa mediante su identificador interno.
     *
     * Si la tarifa existe, Optional.map ejecuta convertirADTO.
     * Si no existe, se conserva un Optional vacío para que
     * el controlador pueda devolver HTTP 404.
     *
     * @param id identificador de la tarifa
     * @return Optional con la tarifa convertida a DTO
     */
    public Optional<TarifaResponseDTO> buscarPorId(
            Long id
    ) {
        return tarifaRepository.findById(id)
                .map(this::convertirADTO);
    }

    /**
     * Consulta el historial de tarifas asociado a un tipo de predio.
     *
     * El historial se obtiene ordenado desde la tarifa cuya fecha
     * de inicio es más reciente hacia la más antigua.
     *
     * @param tipoPredioId identificador del tipo de predio
     * @return historial convertido a TarifaResponseDTO
     */
    public List<TarifaResponseDTO> buscarHistorialPorTipoPredio(
            Long tipoPredioId
    ) {
        return tarifaRepository
                .findByTipoPredioIdOrderByFechaInicioDesc(
                        tipoPredioId
                )
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Busca la tarifa que se encontraba vigente para un tipo
     * de predio en una fecha determinada.
     *
     * @param tipoPredioId identificador del tipo de predio
     * @param fecha fecha que se desea consultar
     * @return Optional con la tarifa vigente convertida a DTO
     */
    public Optional<TarifaResponseDTO> buscarTarifaVigente(
            Long tipoPredioId,
            LocalDate fecha
    ) {
        return tarifaRepository
                .buscarTarifaVigente(tipoPredioId, fecha)
                .map(this::convertirADTO);
    }

    /**
     * Registra una nueva tarifa.
     *
     * REGLAS DE NEGOCIO CONSERVADAS:
     *
     * 1. El tipo de predio es obligatorio y debe existir.
     * 2. El valor debe ser mayor que cero.
     * 3. La fecha de inicio es obligatoria.
     * 4. La fecha final no puede ser anterior a la inicial.
     * 5. La nueva tarifa debe comenzar después de la tarifa anterior.
     * 6. Si la tarifa anterior continúa abierta o se superpone,
     *    se cierra un día antes del inicio de la nueva tarifa.
     *
     * @param dto datos necesarios para registrar la tarifa
     * @return tarifa creada como TarifaResponseDTO
     */
    @Transactional
    public TarifaResponseDTO registrar(
            TarifaRequestDTO dto
    ) {
        validarDatosTarifa(dto);

        /*
         * El RequestDTO recibe solamente tipoPredioId.
         * El servicio utiliza ese ID para recuperar la entidad real
         * y establecer correctamente la relación JPA.
         */
        TipoPredio tipoPredio = tipoPredioRepository
                .findById(dto.tipoPredioId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El tipo de predio indicado no existe"
                        )
                );

        List<Tarifa> historial = tarifaRepository
                .findByTipoPredioIdOrderByFechaInicioDesc(
                        dto.tipoPredioId()
                );

        /*
         * Si ya existe una tarifa para ese tipo de predio,
         * la primera posición corresponde a la más reciente.
         */
        if (!historial.isEmpty()) {
            Tarifa tarifaAnterior = historial.get(0);

            if (!dto.fechaInicio()
                    .isAfter(tarifaAnterior.getFechaInicio())) {
                throw new IllegalArgumentException(
                        "La fecha de inicio de la nueva tarifa "
                                + "debe ser posterior a la tarifa anterior"
                );
            }

            /*
             * Si la tarifa anterior no tiene fecha final o su fecha
             * final se cruza con la nueva vigencia, se cierra
             * automáticamente el día anterior.
             */
            if (tarifaAnterior.getFechaFin() == null
                    || !tarifaAnterior.getFechaFin()
                    .isBefore(dto.fechaInicio())) {
                tarifaAnterior.setFechaFin(
                        dto.fechaInicio().minusDays(1)
                );

                tarifaRepository.save(tarifaAnterior);
            }
        }

        Tarifa nuevaTarifa = Tarifa.builder()
                .tipoPredio(tipoPredio)
                .valor(dto.valor())
                .fechaInicio(dto.fechaInicio())
                .fechaFin(dto.fechaFin())
                .build();

        Tarifa tarifaGuardada =
                tarifaRepository.save(nuevaTarifa);

        return convertirADTO(tarifaGuardada);
    }

    /**
     * Actualiza una tarifa existente.
     *
     * Primero comprueba que la tarifa exista y después valida
     * los nuevos datos recibidos mediante TarifaRequestDTO.
     *
     * La relación con TipoPredio se vuelve a buscar por ID,
     * evitando que el cliente envíe una entidad incompleta.
     *
     * @param id identificador de la tarifa que se actualizará
     * @param dto nuevos datos de la tarifa
     * @return tarifa actualizada como TarifaResponseDTO
     */
    @Transactional
    public TarifaResponseDTO actualizar(
            Long id,
            TarifaRequestDTO dto
    ) {
        Tarifa tarifaExistente = tarifaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe una tarifa con el ID: " + id
                        )
                );

        validarDatosTarifa(dto);

        TipoPredio tipoPredio = tipoPredioRepository
                .findById(dto.tipoPredioId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El tipo de predio indicado no existe"
                        )
                );

        tarifaExistente.setTipoPredio(tipoPredio);
        tarifaExistente.setValor(dto.valor());
        tarifaExistente.setFechaInicio(dto.fechaInicio());
        tarifaExistente.setFechaFin(dto.fechaFin());

        Tarifa tarifaActualizada =
                tarifaRepository.save(tarifaExistente);

        return convertirADTO(tarifaActualizada);
    }

    /**
     * Elimina una tarifa mediante su identificador.
     *
     * Antes de eliminar, comprueba que el registro exista.
     *
     * @param id identificador de la tarifa que se eliminará
     */
    @Transactional
    public void eliminar(Long id) {
        if (!tarifaRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "No existe una tarifa con el ID: " + id
            );
        }

        tarifaRepository.deleteById(id);
    }

    /**
     * Valida los datos recibidos para registrar o actualizar
     * una tarifa.
     *
     * La validación trabaja directamente con TarifaRequestDTO,
     * porque es el contrato de entrada actual del servicio.
     *
     * @param dto datos de la tarifa que se deben validar
     */
    private void validarDatosTarifa(
            TarifaRequestDTO dto
    ) {
        if (dto == null) {
            throw new IllegalArgumentException(
                    "Los datos de la tarifa son obligatorios"
            );
        }

        if (dto.tipoPredioId() == null) {
            throw new IllegalArgumentException(
                    "La tarifa debe tener un tipo de predio"
            );
        }

        if (dto.valor() == null
                || dto.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El valor de la tarifa debe ser mayor que cero"
            );
        }

        if (dto.fechaInicio() == null) {
            throw new IllegalArgumentException(
                    "La fecha de inicio es obligatoria"
            );
        }

        if (dto.fechaFin() != null
                && dto.fechaFin().isBefore(dto.fechaInicio())) {
            throw new IllegalArgumentException(
                    "La fecha final no puede ser anterior "
                            + "a la fecha de inicio"
            );
        }
    }

    /**
     * Convierte una entidad Tarifa en TarifaResponseDTO.
     *
     * La respuesta incluye un TipoPredioResponseDTO para que
     * el cliente pueda identificar claramente el tipo de predio
     * sin recibir la entidad JPA directamente.
     *
     * @param tarifa entidad que se desea convertir
     * @return tarifa preparada para la respuesta de la API
     */
    public TarifaResponseDTO convertirADTO(
            Tarifa tarifa
    ) {
        TipoPredioResponseDTO tipoPredioDTO =
                convertirTipoPredioADTO(
                        tarifa.getTipoPredio()
                );

        return new TarifaResponseDTO(
                tarifa.getId(),
                tipoPredioDTO,
                tarifa.getValor(),
                tarifa.getFechaInicio(),
                tarifa.getFechaFin()
        );
    }

    /**
     * Convierte la entidad TipoPredio relacionada con la tarifa
     * en TipoPredioResponseDTO.
     *
     * Este método mantiene la conversión dentro del servicio
     * y evita exponer directamente la entidad relacionada.
     *
     * @param tipoPredio entidad relacionada con la tarifa
     * @return representación pública del tipo de predio
     */
    private TipoPredioResponseDTO convertirTipoPredioADTO(
            TipoPredio tipoPredio
    ) {
        if (tipoPredio == null) {
            return null;
        }

        return new TipoPredioResponseDTO(
                tipoPredio.getId(),
                tipoPredio.getNombre(),
                tipoPredio.getDescripcion()
        );
    }
}
