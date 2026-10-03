package com.cesde.aguas_paraiso.service;

import com.cesde.aguas_paraiso.model.entity.Factura;
import com.cesde.aguas_paraiso.model.entity.Pago;
import com.cesde.aguas_paraiso.model.enums.EstadoFactura;
import com.cesde.aguas_paraiso.repository.FacturaRepository;
import com.cesde.aguas_paraiso.repository.PagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PagoService {

    private final PagoRepository pagoRepository;
    private final FacturaRepository facturaRepository;

    public PagoService(
            PagoRepository pagoRepository,
            FacturaRepository facturaRepository) {
        this.pagoRepository = pagoRepository;
        this.facturaRepository = facturaRepository;
    }

    public List<Pago> listarTodos() {
        return pagoRepository.findAll();
    }

    public Optional<Pago> buscarPorId(Long id) {
        return pagoRepository.findById(id);
    }

    public List<Pago> buscarPorFactura(Long facturaId) {
        return pagoRepository
                .findByFacturaIdOrderByFechaPagoDesc(
                        facturaId);
    }

    public List<Pago> buscarPorUsuario(Long usuarioId) {
        return pagoRepository
                .findByFacturaPredioPropietarioIdOrderByFechaPagoDesc(
                        usuarioId);
    }

    public List<Pago> buscarPorCedula(
            String cedula) {

        validarCedula(cedula);

        return pagoRepository
                .findByFacturaCedulaPropietarioOrderByFechaPagoDesc(
                        cedula);
    }

    public List<Pago> buscarPorCedulaYRangoDeFechas(
            String cedula,
            LocalDate fechaInicial,
            LocalDate fechaFinal) {

        validarCedula(cedula);
        validarRangoFechas(fechaInicial, fechaFinal);

        return pagoRepository
                .findByFacturaCedulaPropietarioAndFechaPagoBetweenOrderByFechaPagoDesc(
                        cedula,
                        fechaInicial,
                        fechaFinal);
    }

    public BigDecimal calcularTotalPagadoPorCedula(
            String cedula) {

        validarCedula(cedula);

        return pagoRepository.sumarPagosPorCedula(
                cedula);
    }

    public BigDecimal calcularTotalPagadoPorFactura(
            Long facturaId) {

        validarExistenciaFactura(facturaId);

        return pagoRepository.sumarPagosPorFactura(
                facturaId);
    }

    public BigDecimal calcularSaldoPendiente(
            Long facturaId) {

        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una factura con el ID: "
                                + facturaId));

        BigDecimal totalPagado = pagoRepository.sumarPagosPorFactura(
                facturaId);

        BigDecimal saldoPendiente = factura.getValorFactura()
                .subtract(totalPagado);

        if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return saldoPendiente;
    }

    public BigDecimal calcularTotalPagadoPorUsuario(
            Long usuarioId) {
        return pagoRepository.sumarPagosPorUsuario(
                usuarioId);
    }

    @Transactional
    public Pago registrar(Pago pago) {

        validarDatosPago(pago);

        Long facturaId = pago.getFactura().getId();

        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una factura con el ID: "
                                + facturaId));

        if (factura.getEstado() == EstadoFactura.PAGADA) {
            throw new IllegalArgumentException(
                    "La factura ya se encuentra completamente pagada");
        }

        BigDecimal totalPagadoAnterior = pagoRepository.sumarPagosPorFactura(
                facturaId);

        BigDecimal saldoPendiente = factura.getValorFactura()
                .subtract(totalPagadoAnterior);

        if (pago.getValorPagado()
                .compareTo(saldoPendiente) > 0) {

            throw new IllegalArgumentException(
                    "El valor del pago supera el saldo pendiente. "
                            + "Saldo pendiente: "
                            + saldoPendiente);
        }

        pago.setFactura(factura);

        Pago pagoGuardado = pagoRepository.save(pago);

        BigDecimal nuevoTotalPagado = totalPagadoAnterior.add(
                pago.getValorPagado());

        actualizarEstadoFactura(
                factura,
                nuevoTotalPagado);

        return pagoGuardado;
    }

    @Transactional
    public void eliminar(Long id) {

        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un pago con el ID: " + id));

        Factura factura = pago.getFactura();

        pagoRepository.delete(pago);
        pagoRepository.flush();

        BigDecimal totalPagadoRestante = pagoRepository.sumarPagosPorFactura(
                factura.getId());

        actualizarEstadoFactura(
                factura,
                totalPagadoRestante);
    }

    private void actualizarEstadoFactura(
            Factura factura,
            BigDecimal totalPagado) {

        if (totalPagado.compareTo(
                factura.getValorFactura()) >= 0) {

            factura.setEstado(
                    EstadoFactura.PAGADA);

        } else if (totalPagado.compareTo(
                BigDecimal.ZERO) > 0) {

            factura.setEstado(
                    EstadoFactura.PARCIAL);

        } else if (LocalDate.now().isAfter(
                factura.getFechaVencimiento())) {

            factura.setEstado(
                    EstadoFactura.VENCIDA);

        } else {

            factura.setEstado(
                    EstadoFactura.PENDIENTE);
        }

        facturaRepository.save(factura);
    }

    private void validarDatosPago(Pago pago) {

        if (pago == null) {
            throw new IllegalArgumentException(
                    "Los datos del pago son obligatorios");
        }

        if (pago.getFactura() == null
                || pago.getFactura().getId() == null) {

            throw new IllegalArgumentException(
                    "El pago debe estar asociado a una factura");
        }

        if (pago.getValorPagado() == null
                || pago.getValorPagado()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El valor pagado debe ser mayor que cero");
        }

        if (pago.getFechaPago() == null) {
            throw new IllegalArgumentException(
                    "La fecha del pago es obligatoria");
        }

        if (pago.getFechaPago().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha del pago no puede ser futura");
        }

        if (pago.getMetodoPago() == null) {
            throw new IllegalArgumentException(
                    "El método de pago es obligatorio");
        }
    }

    private void validarExistenciaFactura(Long facturaId) {

        if (facturaId == null) {
            throw new IllegalArgumentException(
                    "El ID de la factura es obligatorio");
        }

        if (!facturaRepository.existsById(facturaId)) {
            throw new IllegalArgumentException(
                    "No existe una factura con el ID: "
                            + facturaId);
        }
    }

    private void validarCedula(String cedula) {

        if (cedula == null || cedula.isBlank()) {
            throw new IllegalArgumentException(
                    "La cédula es obligatoria");
        }
    }

    private void validarRangoFechas(
            LocalDate fechaInicial,
            LocalDate fechaFinal) {

        if (fechaInicial == null || fechaFinal == null) {
            throw new IllegalArgumentException(
                    "La fecha inicial y la fecha final son obligatorias");
        }

        if (fechaFinal.isBefore(fechaInicial)) {
            throw new IllegalArgumentException(
                    "La fecha final no puede ser anterior a la fecha inicial");
        }
    }
}