package com.cesde.aguas_paraiso.service;

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

@Service
@Transactional(readOnly = true)
public class TarifaService {

    private final TarifaRepository tarifaRepository;
    private final TipoPredioRepository tipoPredioRepository;

    public TarifaService(
            TarifaRepository tarifaRepository,
            TipoPredioRepository tipoPredioRepository) {
        this.tarifaRepository = tarifaRepository;
        this.tipoPredioRepository = tipoPredioRepository;
    }

    public List<Tarifa> listarTodas() {
        return tarifaRepository.findAll();
    }

    public Optional<Tarifa> buscarPorId(Long id) {
        return tarifaRepository.findById(id);
    }

    public List<Tarifa> buscarHistorialPorTipoPredio(
            Long tipoPredioId) {
        return tarifaRepository
                .findByTipoPredioIdOrderByFechaInicioDesc(
                        tipoPredioId);
    }

    public Optional<Tarifa> buscarTarifaVigente(
            Long tipoPredioId,
            LocalDate fecha) {
        return tarifaRepository.buscarTarifaVigente(
                tipoPredioId,
                fecha);
    }

    @Transactional
    public Tarifa registrar(Tarifa nuevaTarifa) {

        validarDatosTarifa(nuevaTarifa);

        Long tipoPredioId = nuevaTarifa.getTipoPredio().getId();

        TipoPredio tipoPredio = tipoPredioRepository
                .findById(tipoPredioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El tipo de predio indicado no existe"));

        List<Tarifa> historial = tarifaRepository
                .findByTipoPredioIdOrderByFechaInicioDesc(
                        tipoPredioId);

        if (!historial.isEmpty()) {

            Tarifa tarifaAnterior = historial.get(0);

            if (!nuevaTarifa.getFechaInicio()
                    .isAfter(tarifaAnterior.getFechaInicio())) {

                throw new IllegalArgumentException(
                        "La fecha de inicio de la nueva tarifa "
                                + "debe ser posterior a la tarifa anterior");
            }

            if (tarifaAnterior.getFechaFin() == null
                    || !tarifaAnterior.getFechaFin()
                            .isBefore(nuevaTarifa.getFechaInicio())) {

                tarifaAnterior.setFechaFin(
                        nuevaTarifa.getFechaInicio().minusDays(1));

                tarifaRepository.save(tarifaAnterior);
            }
        }

        nuevaTarifa.setTipoPredio(tipoPredio);

        return tarifaRepository.save(nuevaTarifa);
    }

    @Transactional
    public Tarifa actualizar(
            Long id,
            Tarifa datosActualizados) {

        Tarifa tarifaExistente = tarifaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una tarifa con el ID: " + id));

        validarDatosTarifa(datosActualizados);

        Long tipoPredioId = datosActualizados.getTipoPredio().getId();

        TipoPredio tipoPredio = tipoPredioRepository
                .findById(tipoPredioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El tipo de predio indicado no existe"));

        tarifaExistente.setTipoPredio(tipoPredio);
        tarifaExistente.setValor(datosActualizados.getValor());
        tarifaExistente.setFechaInicio(
                datosActualizados.getFechaInicio());
        tarifaExistente.setFechaFin(
                datosActualizados.getFechaFin());

        return tarifaRepository.save(tarifaExistente);
    }

    @Transactional
    public void eliminar(Long id) {

        if (!tarifaRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "No existe una tarifa con el ID: " + id);
        }

        tarifaRepository.deleteById(id);
    }

    private void validarDatosTarifa(Tarifa tarifa) {

        if (tarifa.getTipoPredio() == null
                || tarifa.getTipoPredio().getId() == null) {

            throw new IllegalArgumentException(
                    "La tarifa debe tener un tipo de predio");
        }

        if (tarifa.getValor() == null
                || tarifa.getValor()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El valor de la tarifa debe ser mayor que cero");
        }

        if (tarifa.getFechaInicio() == null) {
            throw new IllegalArgumentException(
                    "La fecha de inicio es obligatoria");
        }

        if (tarifa.getFechaFin() != null
                && tarifa.getFechaFin()
                        .isBefore(tarifa.getFechaInicio())) {

            throw new IllegalArgumentException(
                    "La fecha final no puede ser anterior "
                            + "a la fecha de inicio");
        }
    }
}