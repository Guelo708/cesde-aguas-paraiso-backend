package com.cesde.aguas_paraiso.service;

import com.cesde.aguas_paraiso.dto.PagoRequestDTO;
import com.cesde.aguas_paraiso.dto.PagoResponseDTO;
import com.cesde.aguas_paraiso.model.entity.Factura;
import com.cesde.aguas_paraiso.model.entity.Pago;
import com.cesde.aguas_paraiso.model.entity.Predio;
import com.cesde.aguas_paraiso.model.enums.EstadoFactura;
import com.cesde.aguas_paraiso.repository.FacturaRepository;
import com.cesde.aguas_paraiso.repository.PagoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar la lógica de negocio
 * relacionada con los pagos de Aguas Paraíso.
 *
 * PagoRequestDTO recibe:
 *
 * - El ID de la factura.
 * - El valor pagado.
 * - La fecha del pago.
 * - El método de pago.
 * - Una observación opcional.
 *
 * PagoResponseDTO devuelve información del pago y un resumen
 * de la factura relacionada, sin exponer directamente las
 * entidades JPA Pago y Factura.
 *
 * Este servicio también recalcula automáticamente el estado
 * de la factura cuando se registra o elimina un pago.
 */
@Service
@Transactional(readOnly = true)
public class PagoService {

    /**
     * Repositorio utilizado para consultar, registrar
     * y eliminar pagos.
     */
    private final PagoRepository pagoRepository;

    /**
     * Repositorio utilizado para consultar y actualizar
     * la factura asociada con cada pago.
     */
    private final FacturaRepository facturaRepository;

    /**
     * Constructor utilizado por Spring para inyectar
     * automáticamente los repositorios requeridos.
     *
     * @param pagoRepository repositorio de pagos
     * @param facturaRepository repositorio de facturas
     */
    public PagoService(
            PagoRepository pagoRepository,
            FacturaRepository facturaRepository
    ) {
        this.pagoRepository = pagoRepository;
        this.facturaRepository = facturaRepository;
    }

    /**
     * Consulta todos los pagos registrados.
     *
     * Cada entidad Pago se convierte en PagoResponseDTO
     * antes de salir de la capa de servicio.
     *
     * @return lista de pagos convertidos a DTO
     */
    public List<PagoResponseDTO> listarTodos() {
        return convertirListaADTO(
                pagoRepository.findAll()
        );
    }

    /**
     * Busca un pago mediante su identificador interno.
     *
     * @param id identificador del pago
     * @return Optional con el DTO o vacío si no existe
     */
    public Optional<PagoResponseDTO> buscarPorId(Long id) {
        return pagoRepository.findById(id)
                .map(this::convertirADTO);
    }

    /**
     * Consulta los pagos asociados a una factura,
     * ordenados desde el más reciente.
     *
     * @param facturaId identificador de la factura
     * @return lista de pagos convertidos a DTO
     */
    public List<PagoResponseDTO> buscarPorFactura(
            Long facturaId
    ) {
        validarExistenciaFactura(facturaId);

        return convertirListaADTO(
                pagoRepository
                        .findByFacturaIdOrderByFechaPagoDesc(
                                facturaId
                        )
        );
    }

    /**
     * Consulta los pagos asociados a las facturas
     * pertenecientes a los predios de un usuario.
     *
     * @param usuarioId identificador del usuario
     * @return lista de pagos convertidos a DTO
     */
    public List<PagoResponseDTO> buscarPorUsuario(
            Long usuarioId
    ) {
        return convertirListaADTO(
                pagoRepository
                        .findByFacturaPredioPropietarioIdOrderByFechaPagoDesc(
                                usuarioId
                        )
        );
    }

    /**
     * Consulta los pagos asociados a una cédula.
     *
     * @param cedula cédula histórica almacenada en la factura
     * @return lista de pagos convertidos a DTO
     */
    public List<PagoResponseDTO> buscarPorCedula(
            String cedula
    ) {
        validarCedula(cedula);

        return convertirListaADTO(
                pagoRepository
                        .findByFacturaCedulaPropietarioOrderByFechaPagoDesc(
                                cedula
                        )
        );
    }

    /**
     * Consulta pagos de una cédula dentro de un rango de fechas.
     *
     * @param cedula cédula del propietario
     * @param fechaInicial fecha inicial inclusiva
     * @param fechaFinal fecha final inclusiva
     * @return pagos encontrados convertidos a DTO
     */
    public List<PagoResponseDTO> buscarPorCedulaYRangoDeFechas(
            String cedula,
            LocalDate fechaInicial,
            LocalDate fechaFinal
    ) {
        validarCedula(cedula);
        validarRangoFechas(fechaInicial, fechaFinal);

        return convertirListaADTO(
                pagoRepository
                        .findByFacturaCedulaPropietarioAndFechaPagoBetweenOrderByFechaPagoDesc(
                                cedula,
                                fechaInicial,
                                fechaFinal
                        )
        );
    }

    /**
     * Calcula la suma de todos los pagos asociados a una cédula.
     */
    public BigDecimal calcularTotalPagadoPorCedula(
            String cedula
    ) {
        validarCedula(cedula);

        return normalizarTotal(
                pagoRepository.sumarPagosPorCedula(cedula)
        );
    }

    /**
     * Calcula la suma de los pagos registrados para una factura.
     */
    public BigDecimal calcularTotalPagadoPorFactura(
            Long facturaId
    ) {
        validarExistenciaFactura(facturaId);

        return normalizarTotal(
                pagoRepository.sumarPagosPorFactura(facturaId)
        );
    }

    /**
     * Calcula el saldo pendiente de una factura.
     *
     * Si por cualquier circunstancia la resta produce un valor
     * negativo, el método devuelve cero.
     */
    public BigDecimal calcularSaldoPendiente(
            Long facturaId
    ) {
        Factura factura = buscarFactura(facturaId);

        BigDecimal totalPagado = normalizarTotal(
                pagoRepository.sumarPagosPorFactura(facturaId)
        );

        BigDecimal saldoPendiente = factura
                .getValorFactura()
                .subtract(totalPagado);

        if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return saldoPendiente;
    }

    /**
     * Calcula la suma de todos los pagos asociados
     * a las facturas de un usuario.
     */
    public BigDecimal calcularTotalPagadoPorUsuario(
            Long usuarioId
    ) {
        return normalizarTotal(
                pagoRepository.sumarPagosPorUsuario(usuarioId)
        );
    }

    /**
     * Registra un nuevo pago mediante PagoRequestDTO.
     *
     * REGLAS DE NEGOCIO CONSERVADAS:
     *
     * 1. La factura debe existir.
     * 2. La factura no puede estar completamente pagada.
     * 3. El valor pagado debe ser mayor que cero.
     * 4. La fecha del pago es obligatoria y no puede ser futura.
     * 5. El método de pago es obligatorio.
     * 6. El pago no puede superar el saldo pendiente.
     * 7. Después del pago se actualiza el estado de la factura.
     *
     * @param dto datos necesarios para registrar el pago
     * @return pago registrado como PagoResponseDTO
     */
    @Transactional
    public PagoResponseDTO registrar(
            PagoRequestDTO dto
    ) {
        validarDatosPago(dto);

        Factura factura = buscarFactura(dto.facturaId());

        if (factura.getEstado() == EstadoFactura.PAGADA) {
            throw new IllegalArgumentException(
                    "La factura ya se encuentra completamente pagada"
            );
        }

        BigDecimal totalPagadoAnterior = normalizarTotal(
                pagoRepository.sumarPagosPorFactura(
                        dto.facturaId()
                )
        );

        BigDecimal saldoPendiente = factura
                .getValorFactura()
                .subtract(totalPagadoAnterior);

        if (dto.valorPagado().compareTo(saldoPendiente) > 0) {
            throw new IllegalArgumentException(
                    "El valor del pago supera el saldo pendiente. "
                            + "Saldo pendiente: "
                            + saldoPendiente
            );
        }

        Pago pago = Pago.builder()
                .factura(factura)
                .valorPagado(dto.valorPagado())
                .fechaPago(dto.fechaPago())
                .metodoPago(dto.metodoPago())
                .observacion(normalizarObservacion(dto.observacion()))
                .build();

        Pago pagoGuardado = pagoRepository.save(pago);

        BigDecimal nuevoTotalPagado = totalPagadoAnterior.add(
                dto.valorPagado()
        );

        actualizarEstadoFactura(
                factura,
                nuevoTotalPagado
        );

        return convertirADTO(pagoGuardado);
    }

    /**
     * Elimina un pago y recalcula el estado de la factura.
     *
     * Después de eliminar se utiliza flush() para forzar que
     * la eliminación llegue a la base de datos antes de volver
     * a calcular el total pagado restante.
     *
     * @param id identificador del pago que se eliminará
     */
    @Transactional
    public void eliminar(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe un pago con el ID: " + id
                        )
                );

        Factura factura = pago.getFactura();

        pagoRepository.delete(pago);
        pagoRepository.flush();

        BigDecimal totalPagadoRestante = normalizarTotal(
                pagoRepository.sumarPagosPorFactura(
                        factura.getId()
                )
        );

        actualizarEstadoFactura(
                factura,
                totalPagadoRestante
        );
    }

    /**
     * Actualiza el estado de una factura según el total pagado.
     *
     * - PAGADA: total pagado igual o superior al valor facturado.
     * - PARCIAL: existe un abono, pero aún queda saldo.
     * - VENCIDA: no hay pagos y la fecha límite ya pasó.
     * - PENDIENTE: no hay pagos y todavía no ha vencido.
     */
    private void actualizarEstadoFactura(
            Factura factura,
            BigDecimal totalPagado
    ) {
        if (totalPagado.compareTo(
                factura.getValorFactura()
        ) >= 0) {
            factura.setEstado(EstadoFactura.PAGADA);
        } else if (totalPagado.compareTo(BigDecimal.ZERO) > 0) {
            factura.setEstado(EstadoFactura.PARCIAL);
        } else if (LocalDate.now().isAfter(
                factura.getFechaVencimiento()
        )) {
            factura.setEstado(EstadoFactura.VENCIDA);
        } else {
            factura.setEstado(EstadoFactura.PENDIENTE);
        }

        facturaRepository.save(factura);
    }

    /**
     * Convierte una entidad Pago en PagoResponseDTO.
     *
     * La respuesta incluye los datos esenciales de la factura,
     * del predio y del propietario, sin devolver objetos JPA.
     */
    public PagoResponseDTO convertirADTO(Pago pago) {
        Factura factura = pago.getFactura();
        Predio predio = factura != null
                ? factura.getPredio()
                : null;

        return new PagoResponseDTO(
                pago.getId(),
                factura != null ? factura.getId() : null,
                predio != null ? predio.getCodigoPredio() : null,
                factura != null
                        ? factura.getCedulaPropietario()
                        : null,
                factura != null
                        ? factura.getNombrePropietario()
                        : null,
                factura != null ? factura.getMes() : null,
                factura != null ? factura.getAnio() : null,
                factura != null
                        ? factura.getValorFactura()
                        : null,
                pago.getValorPagado(),
                pago.getFechaPago(),
                pago.getMetodoPago(),
                pago.getObservacion(),
                factura != null && factura.getEstado() != null
                        ? factura.getEstado().name()
                        : null,
                pago.getFechaCreacion(),
                pago.getFechaActualizacion()
        );
    }

    /**
     * Convierte una lista de entidades Pago en una lista de DTO.
     */
    private List<PagoResponseDTO> convertirListaADTO(
            List<Pago> pagos
    ) {
        return pagos.stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Busca una factura y produce un mensaje controlado
     * cuando el identificador no existe.
     */
    private Factura buscarFactura(Long facturaId) {
        if (facturaId == null) {
            throw new IllegalArgumentException(
                    "El ID de la factura es obligatorio"
            );
        }

        return facturaRepository.findById(facturaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe una factura con el ID: "
                                        + facturaId
                        )
                );
    }

    /**
     * Valida la existencia de una factura para las consultas
     * que únicamente necesitan comprobar su ID.
     */
    private void validarExistenciaFactura(Long facturaId) {
        buscarFactura(facturaId);
    }

    /**
     * Valida los datos recibidos para registrar un pago.
     */
    private void validarDatosPago(PagoRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException(
                    "Los datos del pago son obligatorios"
            );
        }

        if (dto.facturaId() == null) {
            throw new IllegalArgumentException(
                    "El pago debe estar asociado a una factura"
            );
        }

        if (dto.valorPagado() == null
                || dto.valorPagado().compareTo(
                BigDecimal.ZERO
        ) <= 0) {
            throw new IllegalArgumentException(
                    "El valor pagado debe ser mayor que cero"
            );
        }

        if (dto.fechaPago() == null) {
            throw new IllegalArgumentException(
                    "La fecha del pago es obligatoria"
            );
        }

        if (dto.fechaPago().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha del pago no puede ser futura"
            );
        }

        if (dto.metodoPago() == null) {
            throw new IllegalArgumentException(
                    "El método de pago es obligatorio"
            );
        }
    }

    /**
     * Convierte un total nulo en cero para evitar errores
     * cuando todavía no existen pagos asociados.
     */
    private BigDecimal normalizarTotal(BigDecimal total) {
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Convierte una observación vacía en null y elimina
     * espacios innecesarios en los extremos.
     */
    private String normalizarObservacion(String observacion) {
        if (observacion == null || observacion.isBlank()) {
            return null;
        }

        return observacion.trim();
    }

    private void validarCedula(String cedula) {
        if (cedula == null || cedula.isBlank()) {
            throw new IllegalArgumentException(
                    "La cédula es obligatoria"
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
