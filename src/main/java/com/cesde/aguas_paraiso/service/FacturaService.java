package com.cesde.aguas_paraiso.service;

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

@Service
@Transactional(readOnly = true)
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final PredioRepository predioRepository;
    private final TarifaRepository tarifaRepository;
    private final UsuarioRepository usuarioRepository;

    public FacturaService(
            FacturaRepository facturaRepository,
            PredioRepository predioRepository,
            TarifaRepository tarifaRepository,
            UsuarioRepository usuarioRepository) {
        this.facturaRepository = facturaRepository;
        this.predioRepository = predioRepository;
        this.tarifaRepository = tarifaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Factura> listarTodas() {
        return facturaRepository.findAll();
    }

    public Optional<Factura> buscarPorId(Long id) {
        return facturaRepository.findById(id);
    }

    public Optional<Factura> buscarPorPredioYPeriodo(
            Long predioId,
            Integer mes,
            Integer anio) {
        return facturaRepository.findByPredioIdAndMesAndAnio(
                predioId,
                mes,
                anio);
    }

    public List<Factura> buscarPorPredio(Long predioId) {
        return facturaRepository
                .findByPredioIdOrderByAnioDescMesDesc(
                        predioId);
    }

    public List<Factura> buscarPorUsuario(Long usuarioId) {
        return facturaRepository
                .findByPredioPropietarioIdOrderByAnioDescMesDesc(
                        usuarioId);
    }

    public List<Factura> buscarPorCedulaPropietario(
            String cedula) {

        validarCedula(cedula);

        if (!usuarioRepository.existsByCedula(cedula)) {
            throw new IllegalArgumentException(
                    "No existe un usuario registrado con esta cédula");
        }

        return facturaRepository
                .findByCedulaPropietarioOrderByAnioDescMesDesc(
                        cedula);
    }

    public List<Factura> buscarFacturasPorPagarPorCedula(
            String cedula) {

        validarCedula(cedula);

        if (!usuarioRepository.existsByCedula(cedula)) {
            throw new IllegalArgumentException(
                    "No existe un usuario registrado con esta cédula");
        }

        List<Factura> facturasPorPagar = new ArrayList<>();

        facturasPorPagar.addAll(
                facturaRepository
                        .findByCedulaPropietarioAndEstadoOrderByAnioDescMesDesc(
                                cedula,
                                EstadoFactura.PENDIENTE));

        facturasPorPagar.addAll(
                facturaRepository
                        .findByCedulaPropietarioAndEstadoOrderByAnioDescMesDesc(
                                cedula,
                                EstadoFactura.PARCIAL));

        facturasPorPagar.addAll(
                facturaRepository
                        .findByCedulaPropietarioAndEstadoOrderByAnioDescMesDesc(
                                cedula,
                                EstadoFactura.VENCIDA));

        return facturasPorPagar;
    }

    public List<Factura> buscarPorUsuarioYEstado(
            Long usuarioId,
            EstadoFactura estado) {
        return facturaRepository
                .findByPredioPropietarioIdAndEstado(
                        usuarioId,
                        estado);
    }

    public List<Factura> buscarPorCedulaYEstado(
            String cedula,
            EstadoFactura estado) {

        validarCedula(cedula);

        return facturaRepository
                .findByCedulaPropietarioAndEstadoOrderByAnioDescMesDesc(
                        cedula,
                        estado);
    }

    public long contarPorUsuarioYEstado(
            Long usuarioId,
            EstadoFactura estado) {
        return facturaRepository
                .countByPredioPropietarioIdAndEstado(
                        usuarioId,
                        estado);
    }

    public long contarPorCedulaYEstado(
            String cedula,
            EstadoFactura estado) {

        validarCedula(cedula);

        return facturaRepository
                .countByCedulaPropietarioAndEstado(
                        cedula,
                        estado);
    }

    @Transactional
    public Factura generarFactura(
            Long predioId,
            Integer mes,
            Integer anio) {

        validarPeriodo(mes, anio);

        if (facturaRepository.existsByPredioIdAndMesAndAnio(
                predioId,
                mes,
                anio)) {
            throw new IllegalArgumentException(
                    "El predio ya tiene una factura para el periodo "
                            + mes + "/" + anio);
        }

        Predio predio = predioRepository.findById(predioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un predio con el ID: "
                                + predioId));

        if (predio.getTipoPredio() == null
                || predio.getTipoPredio().getId() == null) {

            throw new IllegalArgumentException(
                    "El predio no tiene un tipo de predio asignado");
        }

        if (predio.getPropietario() == null
                || predio.getPropietario().getId() == null) {

            throw new IllegalArgumentException(
                    "El predio no tiene un propietario asignado");
        }

        Usuario propietario = usuarioRepository
                .findById(predio.getPropietario().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "El propietario asignado al predio no existe"));

        LocalDate fechaGeneracion = LocalDate.of(
                anio,
                mes,
                1);

        LocalDate fechaVencimiento = LocalDate.of(
                anio,
                mes,
                15);

        Tarifa tarifaVigente = tarifaRepository
                .buscarTarifaVigente(
                        predio.getTipoPredio().getId(),
                        fechaGeneracion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una tarifa vigente para "
                                + "el tipo de predio en el periodo indicado"));

        String nombreCompleto = construirNombreCompleto(propietario);

        Factura factura = Factura.builder()
                .predio(predio)
                .tarifaAplicada(tarifaVigente)
                .cedulaPropietario(propietario.getCedula())
                .nombrePropietario(nombreCompleto)
                .mes(mes)
                .anio(anio)
                .valorFactura(tarifaVigente.getValor())
                .fechaGeneracion(fechaGeneracion)
                .fechaVencimiento(fechaVencimiento)
                .estado(EstadoFactura.PENDIENTE)
                .build();

        return facturaRepository.save(factura);
    }

    @Transactional
    public List<Factura> generarFacturasMensuales(
            Integer mes,
            Integer anio) {

        validarPeriodo(mes, anio);

        List<Predio> predios = predioRepository.findAll();
        List<Factura> facturasGeneradas = new ArrayList<>();

        for (Predio predio : predios) {

            boolean facturaExistente = facturaRepository
                    .existsByPredioIdAndMesAndAnio(
                            predio.getId(),
                            mes,
                            anio);

            if (!facturaExistente) {
                Factura factura = generarFactura(
                        predio.getId(),
                        mes,
                        anio);

                facturasGeneradas.add(factura);
            }
        }

        return facturasGeneradas;
    }

    @Transactional
    public int actualizarFacturasVencidas() {

        LocalDate fechaActual = LocalDate.now();

        List<Factura> facturasVencidas = facturaRepository
                .findByFechaVencimientoBeforeAndEstadoNot(
                        fechaActual,
                        EstadoFactura.PAGADA);

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

    @Transactional
    public void eliminar(Long id) {

        if (!facturaRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "No existe una factura con el ID: " + id);
        }

        facturaRepository.deleteById(id);
    }

    private String construirNombreCompleto(
            Usuario propietario) {

        StringBuilder nombreCompleto = new StringBuilder();

        agregarParteNombre(
                nombreCompleto,
                propietario.getPrimerNombre());

        agregarParteNombre(
                nombreCompleto,
                propietario.getSegundoNombre());

        agregarParteNombre(
                nombreCompleto,
                propietario.getPrimerApellido());

        agregarParteNombre(
                nombreCompleto,
                propietario.getSegundoApellido());

        return nombreCompleto.toString().trim();
    }

    public List<Factura> buscarPorCedulaYRangoDeFechas(
            String cedula,
            LocalDate fechaInicial,
            LocalDate fechaFinal) {

        validarCedula(cedula);
        validarRangoFechas(fechaInicial, fechaFinal);

        if (!usuarioRepository.existsByCedula(cedula)) {
            throw new IllegalArgumentException(
                    "No existe un usuario registrado con esta cédula");
        }

        return facturaRepository
                .findByCedulaPropietarioAndFechaGeneracionBetweenOrderByFechaGeneracionDesc(
                        cedula,
                        fechaInicial,
                        fechaFinal);
    }

    public List<Factura> buscarPorCedulaEstadoYRangoDeFechas(
            String cedula,
            EstadoFactura estado,
            LocalDate fechaInicial,
            LocalDate fechaFinal) {

        validarCedula(cedula);
        validarRangoFechas(fechaInicial, fechaFinal);

        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado de la factura es obligatorio");
        }

        if (!usuarioRepository.existsByCedula(cedula)) {
            throw new IllegalArgumentException(
                    "No existe un usuario registrado con esta cédula");
        }

        return facturaRepository
                .findByCedulaPropietarioAndEstadoAndFechaGeneracionBetweenOrderByFechaGeneracionDesc(
                        cedula,
                        estado,
                        fechaInicial,
                        fechaFinal);
    }

    private void agregarParteNombre(
            StringBuilder nombreCompleto,
            String parte) {

        if (parte != null && !parte.isBlank()) {

            if (!nombreCompleto.isEmpty()) {
                nombreCompleto.append(" ");
            }

            nombreCompleto.append(parte.trim());
        }
    }

    private void validarCedula(String cedula) {

        if (cedula == null || cedula.isBlank()) {
            throw new IllegalArgumentException(
                    "La cédula es obligatoria");
        }
    }

    private void validarPeriodo(
            Integer mes,
            Integer anio) {

        if (mes == null || mes < 1 || mes > 12) {
            throw new IllegalArgumentException(
                    "El mes debe estar entre 1 y 12");
        }

        if (anio == null || anio <= 0) {
            throw new IllegalArgumentException(
                    "El año debe ser mayor que cero");
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
