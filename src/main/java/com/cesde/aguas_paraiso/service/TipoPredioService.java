package com.cesde.aguas_paraiso.service;

import com.cesde.aguas_paraiso.model.entity.TipoPredio;
import com.cesde.aguas_paraiso.repository.TipoPredioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TipoPredioService {

    private final TipoPredioRepository tipoPredioRepository;

    public TipoPredioService(
            TipoPredioRepository tipoPredioRepository
    ) {
        this.tipoPredioRepository = tipoPredioRepository;
    }

    public List<TipoPredio> listarTodos() {
        return tipoPredioRepository.findAll();
    }

    public Optional<TipoPredio> buscarPorId(Long id) {
        return tipoPredioRepository.findById(id);
    }

    public Optional<TipoPredio> buscarPorNombre(String nombre) {
        return tipoPredioRepository.findByNombreIgnoreCase(nombre);
    }

    public List<TipoPredio> buscarPorNombreParcial(
            String nombre
    ) {
        return tipoPredioRepository
                .findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional
    public TipoPredio registrar(TipoPredio tipoPredio) {

        if (tipoPredioRepository.existsByNombreIgnoreCase(
                tipoPredio.getNombre()
        )) {
            throw new IllegalArgumentException(
                    "Ya existe un tipo de predio registrado con este nombre"
            );
        }

        return tipoPredioRepository.save(tipoPredio);
    }

    @Transactional
    public TipoPredio actualizar(
            Long id,
            TipoPredio datosActualizados
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
                        datosActualizados.getNombre()
                );

        if (tipoPredioConNombre.isPresent()
                && !tipoPredioConNombre.get()
                .getId()
                .equals(id)) {

            throw new IllegalArgumentException(
                    "El nombre ya pertenece a otro tipo de predio"
            );
        }

        tipoPredioExistente.setNombre(
                datosActualizados.getNombre()
        );

        tipoPredioExistente.setDescripcion(
                datosActualizados.getDescripcion()
        );

        return tipoPredioRepository.save(tipoPredioExistente);
    }

    @Transactional
    public void eliminar(Long id) {

        if (!tipoPredioRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "No existe un tipo de predio con el ID: " + id
            );
        }

        tipoPredioRepository.deleteById(id);
    }
}