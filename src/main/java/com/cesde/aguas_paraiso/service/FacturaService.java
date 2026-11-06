package com.cesde.aguas_paraiso.service;

import com.cesde.aguas_paraiso.dto.FacturaRequestDTO;
import com.cesde.aguas_paraiso.dto.FacturaResponseDTO;
import com.cesde.aguas_paraiso.model.entity.Factura;
import com.cesde.aguas_paraiso.model.entity.Predio;
import com.cesde.aguas_paraiso.model.entity.Tarifa;
import com.cesde.aguas_paraiso.model.entity.Usuario;
import com.cesde.aguas_paraiso.model.enums.EstadoFactura;
import com.cesde.aguas_paraiso.repository.FacturaRepository;
import com.cesde.aguas_paraiso.repository.PredioRepository;
import com.cesde.aguas_paraiso.repository.TarifaRepository;
import com.cesde.aguas_paraiso.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar la lógica de negocio
 * relacionada con las facturas de Aguas Paraíso.
 *
 * FacturaRequestDTO recibe únicamente los datos necesarios
 * para generar una factura: predioId, mes y año.
 *
 * El sistema determina automáticamente la tarifa vigente,
 * el valor facturado, los datos históricos del propietario,
 * las fechas y el estado inicial.
 *
 * FacturaResponseDTO evita exponer directamente las entidades
 * Factura, Predio y Tarifa.
 */
@Service
@Transactional(readOnly = true)
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final PredioRepository predioRepository;
    private final TarifaRepository tarifaRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor utilizado por Spring para inyectar
     * los repositorios requeridos por este servicio.
     */
    public FacturaService(
            FacturaRepository facturaRepository,
            PredioRepository predioRepository,
            TarifaRepository tarifaRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.facturaRepository = facturaRepository;
        this.predioRepository = predioRepository;
        this.tarifaRepository = tarifaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Consulta todas las facturas y convierte cada entidad
     * en FacturaResponseDTO.
     */
    public List<FacturaResponseDTO> listarTodas() {
        return convertirListaADTO(
                facturaRepository.findAll()
        );
    }

    /**
     * Busca una factura por su identificador interno.
     */
    public Optional<FacturaResponseDTO> buscarPorId(Long id) {
        return facturaRepository.findById(id)
                .map(this::convertirADTO);
    }

    /**
     * Busca la factura de un predio para un mes y año concretos.
     */
    public Optional<FacturaResponseDTO> buscarPorPredioYPeriodo(
            Long predioId,
            Integer mes,
            Integer anio
    ) {
        validarPeriodo(mes, anio);

        return facturaRepository
                .findByPredioIdAndMesAndAnio(
                        predioId,
                        mes,
                        anio
                )
                .map(this::convertirADTO);
    }

    /**
     * Consulta el historial de facturas de un predio.
     */
    public List<FacturaResponseDTO> buscarPorPredio(
            Long predioId
    ) {
        return convertirListaADTO(
                facturaRepository
                        .findByPredioIdOrderByAnioDescMesDesc(
                                predioId
                        )
        );
    }

    /**
     * Consulta las facturas asociadas a los predios de un usuario.
     */
    public List<FacturaResponseDTO> buscarPorUsuario(
            Long usuarioId
    ) {
        return convertirListaADTO(
                facturaRepository
                        .findByPredioPropietarioIdOrderByAnioDescMesDesc(
                                usuarioId
                        )
        );
    }

    /**
     * Consulta las facturas utilizando la cédula histórica
     * almacenada en cada factura.
     */
    public List<FacturaResponseDTO> buscarPorCedulaPropietario(
            String cedula
    ) {
        validarUsuarioPorCedula(cedula);

        return convertirListaADTO(
                facturaRepository
                        .findByCedulaPropietarioOrderByAnioDescMesDesc(
                                cedula
                        )
        );
    }

    /**
     * Reúne las facturas pendientes, parciales y vencidas
     * asociadas a una cédula.
     */
    public List<FacturaResponseDTO> buscarFacturasPorPagarPorCedula(
            String cedula
    ) {
        validarUsuarioPorCedula(cedula);

        List<Factura> facturasPorPagar = new ArrayList<>();

        facturasPorPagar.addAll(
                facturaRepository
                        .findByCedulaPropietarioAndEstadoOrderByAnioDescMesDesc(
                                cedula,
                                EstadoFactura.PENDIENTE
                        )
        );

        facturasPorPagar.addAll(
                facturaRepository
                        .findByCedulaPropietarioAndEstadoOrderByAnioDescMesDesc(
                                cedula,
                                EstadoFactura.PARCIAL
                        )
        );

        facturasPorPagar.addAll(
                facturaRepository
                        .findByCedulaPropietarioAndEstadoOrderByAnioDescMesDesc(
                                cedula,
                                EstadoFactura.VENCIDA
                        )
        );

        return convertirListaADTO(facturasPorPagar);
    }

    /**
     * Consulta facturas por usuario y estado.
     */
    public List<FacturaResponseDTO> buscarPorUsuarioYEstado(
            Long usuarioId,
            EstadoFactura estado
    ) {
        validarEstado(estado);

        return convertirListaADTO(
                facturaRepository
                        .findByPredioPropietarioIdAndEstado(
                                usuarioId,
                                estado
                        )
        );
    }

    /**
     * Consulta facturas por cédula y estado.
     */
    public List<FacturaResponseDTO> buscarPorCedulaYEstado(
            String cedula,
            EstadoFactura estado
    ) {
        validarCedula(cedula);
        validarEstado(estado);

        return convertirListaADTO(
                facturaRepository
                        .findByCedulaPropietarioAndEstadoOrderByAnioDescMesDesc(
                                cedula,
                                estado
                        )
        );
    }

    /**
     * Cuenta las facturas de un usuario que tienen un estado dado.
     */
    public long contarPorUsuarioYEstado(
            Long usuarioId,
            EstadoFactura estado
    ) {
        validarEstado(estado);

        return facturaRepository
                .countByPredioPropietarioIdAndEstado(
                        usuarioId,
                        estado
                );
    }

    /**
     * Cuenta las facturas de una cédula que tienen un estado dado.
     */
    public long contarPorCedulaYEstado(
            String cedula,
            EstadoFactura estado
    ) {
        validarCedula(cedula);
        validarEstado(estado);

        return facturaRepository
                .countByCedulaPropietarioAndEstado(
                        cedula,
                        estado
                );
    }

    /**
     * Genera una factura usando FacturaRequestDTO.
     *
     * REGLAS CONSERVADAS:
     *
     * 1. El mes debe estar entre 1 y 12.
     * 2. El año debe ser mayor que cero.
     * 3. No puede existir otra factura para el mismo predio y periodo.
     * 4. El predio, su tipo y su propietario deben existir.
     * 5. Debe existir una tarifa vigente para el periodo.
     * 6. El valor se copia desde la tarifa vigente.
     * 7. El estado inicial es PENDIENTE.
     */
    @Transactional
    public FacturaResponseDTO generarFactura(
            FacturaRequestDTO dto
    ) {
        validarDatosGeneracion(dto);

        Factura facturaGuardada = generarFacturaEntidad(
                dto.predioId(),
                dto.mes(),
                dto.anio()
        );

        return convertirADTO(facturaGuardada);
    }

    /**
     * Mantiene una sobrecarga con parámetros separados.
     * Es útil para la generación masiva y conserva la lógica
     * centralizada en un único método interno.
     */
    @Transactional
    public FacturaResponseDTO generarFactura(
            Long predioId,
            Integer mes,
            Integer anio
    ) {
        Factura facturaGuardada = generarFacturaEntidad(
                predioId,
                mes,
                anio
        );

        return convertirADTO(facturaGuardada);
    }

    /**
     * Genera las facturas mensuales de todos los predios.
     * Omite los predios que ya tienen factura para ese periodo.
     */
    @Transactional
    public List<FacturaResponseDTO> generarFacturasMensuales(
            Integer mes,
            Integer anio
    ) {
        validarPeriodo(mes, anio);

        List<Predio> predios = predioRepository.findAll();
        List<Factura> facturasGeneradas = new ArrayList<>();

        for (Predio predio : predios) {
            boolean facturaExistente = facturaRepository
                    .existsByPredioIdAndMesAndAnio(
                            predio.getId(),
                            mes,
                            anio
                    );

            if (!facturaExistente) {
                Factura factura = generarFacturaEntidad(
                        predio.getId(),
                        mes,
                        anio
                );

                facturasGeneradas.add(factura);
            }
        }

        return convertirListaADTO(facturasGeneradas);
    }

    /**
     * Marca como VENCIDAS las facturas cuya fecha de vencimiento
     * ya pasó y que no se encuentran PAGADAS.
     *
     * @return cantidad de facturas que cambiaron de estado
     */
    @Transactional
    public int actualizarFacturasVencidas() {
        LocalDate fechaActual = LocalDate.now();

        List<Factura> facturasVencidas = facturaRepository
                .findByFechaVencimientoBeforeAndEstadoNot(
                        fechaActual,
                        EstadoFactura.PAGADA
                );

        int cantidadActualizada = 0;

        for (Factura factura : facturasVencidas) {
            if (factura.getEstado() != EstadoFactura.VENCIDA) {
                factura.setEstado(EstadoFactura.VENCIDA);
                facturaRepository.save(factura);
                cantidadActualizada++;
            }
        }

        return cantidadActualizada;
    }

    /**
     * Elimina una factura después de verificar su existencia.
     */
    @Transactional
    public void eliminar(Long id) {
        if (!facturaRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "No existe una factura con el ID: " + id
            );
        }

        facturaRepository.deleteById(id);
    }

    /**
     * Consulta facturas de una cédula dentro de un rango
     * de fechas de generación.
     */
    public List<FacturaResponseDTO> buscarPorCedulaYRangoDeFechas(
            String cedula,
            LocalDate fechaInicial,
            LocalDate fechaFinal
    ) {
        validarUsuarioPorCedula(cedula);
        validarRangoFechas(fechaInicial, fechaFinal);

        return convertirListaADTO(
                facturaRepository
                        .findByCedulaPropietarioAndFechaGeneracionBetweenOrderByFechaGeneracionDesc(
                                cedula,
                                fechaInicial,
                                fechaFinal
                        )
        );
    }

    /**
     * Consulta facturas por cédula, estado y rango de fechas.
     */
    public List<FacturaResponseDTO>
    buscarPorCedulaEstadoYRangoDeFechas(
            String cedula,
            EstadoFactura estado,
            LocalDate fechaInicial,
            LocalDate fechaFinal
    ) {
        validarUsuarioPorCedula(cedula);
        validarEstado(estado);
        validarRangoFechas(fechaInicial, fechaFinal);

        return convertirListaADTO(
                facturaRepository
                        .findByCedulaPropietarioAndEstadoAndFechaGeneracionBetweenOrderByFechaGeneracionDesc(
                                cedula,
                                estado,
                                fechaInicial,
                                fechaFinal
                        )
        );
    }

    /**
     * Método interno que contiene la generación real de la entidad.
     * Permite reutilizar exactamente las mismas reglas en la
     * generación individual y en la generación masiva.
     */
    private Factura generarFacturaEntidad(
            Long predioId,
            Integer mes,
            Integer anio
    ) {
        if (predioId == null) {
            throw new IllegalArgumentException(
                    "El ID del predio es obligatorio"
            );
        }

        validarPeriodo(mes, anio);

        if (facturaRepository.existsByPredioIdAndMesAndAnio(
                predioId,
                mes,
                anio
        )) {
            throw new IllegalArgumentException(
                    "El predio ya tiene una factura para el periodo "
                            + mes + "/" + anio
            );
        }

        Predio predio = predioRepository.findById(predioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe un predio con el ID: "
                                        + predioId
                        )
                );

        if (predio.getTipoPredio() == null
                || predio.getTipoPredio().getId() == null) {
            throw new IllegalArgumentException(
                    "El predio no tiene un tipo de predio asignado"
            );
        }

        if (predio.getPropietario() == null
                || predio.getPropietario().getId() == null) {
            throw new IllegalArgumentException(
                    "El predio no tiene un propietario asignado"
            );
        }

        Usuario propietario = usuarioRepository
                .findById(predio.getPropietario().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El propietario asignado al predio no existe"
                        )
                );

        LocalDate fechaGeneracion = LocalDate.of(
                anio,
                mes,
                1
        );

        LocalDate fechaVencimiento = LocalDate.of(
                anio,
                mes,
                15
        );

        Tarifa tarifaVigente = tarifaRepository
                .buscarTarifaVigente(
                        predio.getTipoPredio().getId(),
                        fechaGeneracion
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe una tarifa vigente para "
                                        + "el tipo de predio en el periodo indicado"
                        )
                );

        Factura factura = Factura.builder()
                .predio(predio)
                .tarifaAplicada(tarifaVigente)
                .cedulaPropietario(propietario.getCedula())
                .nombrePropietario(
                        construirNombreCompleto(propietario)
                )
                .mes(mes)
                .anio(anio)
                .valorFactura(tarifaVigente.getValor())
                .fechaGeneracion(fechaGeneracion)
                .fechaVencimiento(fechaVencimiento)
                .estado(EstadoFactura.PENDIENTE)
                .build();

        return facturaRepository.save(factura);
    }

    /**
     * Convierte una entidad Factura en FacturaResponseDTO.
     */
    public FacturaResponseDTO convertirADTO(
            Factura factura
    ) {
        Predio predio = factura.getPredio();
        Tarifa tarifa = factura.getTarifaAplicada();

        return new FacturaResponseDTO(
                factura.getId(),
                predio != null ? predio.getId() : null,
                predio != null ? predio.getCodigoPredio() : null,
                tarifa != null ? tarifa.getId() : null,
                tarifa != null && tarifa.getTipoPredio() != null
                        ? tarifa.getTipoPredio().getNombre()
                        : null,
                factura.getCedulaPropietario(),
                factura.getNombrePropietario(),
                factura.getMes(),
                factura.getAnio(),
                factura.getValorFactura(),
                factura.getFechaGeneracion(),
                factura.getFechaVencimiento(),
                factura.getEstado() != null
                        ? factura.getEstado().name()
                        : null,
                factura.getFechaCreacion(),
                factura.getFechaActualizacion()
        );
    }

    /**
     * Convierte una lista de entidades Factura en una lista de DTO.
     */
    private List<FacturaResponseDTO> convertirListaADTO(
            List<Factura> facturas
    ) {
        return facturas.stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Construye el nombre completo del propietario sin espacios
     * innecesarios cuando alguna parte está vacía.
     */
    private String construirNombreCompleto(
            Usuario propietario
    ) {
        StringBuilder nombreCompleto = new StringBuilder();

        agregarParteNombre(
                nombreCompleto,
                propietario.getPrimerNombre()
        );
        agregarParteNombre(
                nombreCompleto,
                propietario.getSegundoNombre()
        );
        agregarParteNombre(
                nombreCompleto,
                propietario.getPrimerApellido()
        );
        agregarParteNombre(
                nombreCompleto,
                propietario.getSegundoApellido()
        );

        return nombreCompleto.toString().trim();
    }

    private void agregarParteNombre(
            StringBuilder nombreCompleto,
            String parte
    ) {
        if (parte != null && !parte.isBlank()) {
            if (!nombreCompleto.isEmpty()) {
                nombreCompleto.append(" ");
            }

            nombreCompleto.append(parte.trim());
        }
    }

    /**
     * Valida el DTO utilizado para generar una factura individual.
     */
    private void validarDatosGeneracion(
            FacturaRequestDTO dto
    ) {
        if (dto == null) {
            throw new IllegalArgumentException(
                    "Los datos de la factura son obligatorios"
            );
        }

        if (dto.predioId() == null) {
            throw new IllegalArgumentException(
                    "El ID del predio es obligatorio"
            );
        }

        validarPeriodo(dto.mes(), dto.anio());
    }

    private void validarUsuarioPorCedula(
            String cedula
    ) {
        validarCedula(cedula);

        if (!usuarioRepository.existsByCedula(cedula)) {
            throw new IllegalArgumentException(
                    "No existe un usuario registrado con esta cédula"
            );
        }
    }

    private void validarCedula(String cedula) {
        if (cedula == null || cedula.isBlank()) {
            throw new IllegalArgumentException(
                    "La cédula es obligatoria"
            );
        }
    }

    private void validarEstado(
            EstadoFactura estado
    ) {
        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado de la factura es obligatorio"
            );
        }
    }

    private void validarPeriodo(
            Integer mes,
            Integer anio
    ) {
        if (mes == null || mes < 1 || mes > 12) {
            throw new IllegalArgumentException(
                    "El mes debe estar entre 1 y 12"
            );
        }

        if (anio == null || anio <= 0) {
            throw new IllegalArgumentException(
                    "El año debe ser mayor que cero"
            );
        }
    }

    private void validarRangoFechas(
            LocalDate fechaInicial,
            LocalDate fechaFinal
    ) {
        if (fechaInicial == null || fechaFinal == null) {
            throw new IllegalArgumentException(
                    "La fecha inicial y la fecha final son obligatorias"
            );
        }

        if (fechaFinal.isBefore(fechaInicial)) {
            throw new IllegalArgumentException(
                    "La fecha final no puede ser anterior a la fecha inicial"
            );
        }
    }
}
