package com.cesde.aguas_paraiso.service;

import com.cesde.aguas_paraiso.model.entity.Predio;
import com.cesde.aguas_paraiso.model.entity.Sector;
import com.cesde.aguas_paraiso.model.entity.TipoPredio;
import com.cesde.aguas_paraiso.model.entity.Usuario;
import com.cesde.aguas_paraiso.repository.PredioRepository;
import com.cesde.aguas_paraiso.repository.SectorRepository;
import com.cesde.aguas_paraiso.repository.TipoPredioRepository;
import com.cesde.aguas_paraiso.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PredioService {

    private final PredioRepository predioRepository;
    private final UsuarioRepository usuarioRepository;
    private final SectorRepository sectorRepository;
    private final TipoPredioRepository tipoPredioRepository;

    public PredioService(
            PredioRepository predioRepository,
            UsuarioRepository usuarioRepository,
            SectorRepository sectorRepository,
            TipoPredioRepository tipoPredioRepository
    ) {
        this.predioRepository = predioRepository;
        this.usuarioRepository = usuarioRepository;
        this.sectorRepository = sectorRepository;
        this.tipoPredioRepository = tipoPredioRepository;
    }

    public List<Predio> listarTodos() {
        return predioRepository.findAll();
    }

    public Optional<Predio> buscarPorId(Long id) {
        return predioRepository.findById(id);
    }

    public Optional<Predio> buscarPorCodigo(String codigoPredio) {
        return predioRepository.findByCodigoPredio(codigoPredio);
    }

    public List<Predio> buscarPorPropietario(Long usuarioId) {
        return predioRepository.findByPropietarioId(usuarioId);
    }

    public List<Predio> buscarPorSector(Long sectorId) {
        return predioRepository.findBySectorId(sectorId);
    }

    public List<Predio> buscarPorTipoPredio(Long tipoPredioId) {
        return predioRepository.findByTipoPredioId(tipoPredioId);
    }

    @Transactional
    public Predio registrar(Predio predio) {

        if (predioRepository.existsByCodigoPredio(
                predio.getCodigoPredio()
        )) {
            throw new IllegalArgumentException(
                    "Ya existe un predio registrado con este código"
            );
        }

        validarRelaciones(predio);

        return predioRepository.save(predio);
    }

    @Transactional
    public Predio actualizar(
            Long id,
            Predio datosActualizados
    ) {

        Predio predioExistente = predioRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe un predio con el ID: " + id
                        )
                );

        Optional<Predio> predioConCodigo =
                predioRepository.findByCodigoPredio(
                        datosActualizados.getCodigoPredio()
                );

        if (predioConCodigo.isPresent()
                && !predioConCodigo.get().getId().equals(id)) {

            throw new IllegalArgumentException(
                    "El código del predio ya pertenece a otro registro"
            );
        }

        validarRelaciones(datosActualizados);

        predioExistente.setCodigoPredio(
                datosActualizados.getCodigoPredio()
        );

        predioExistente.setUbicacion(
                datosActualizados.getUbicacion()
        );

        predioExistente.setPropietario(
                datosActualizados.getPropietario()
        );

        predioExistente.setSector(
                datosActualizados.getSector()
        );

        predioExistente.setTipoPredio(
                datosActualizados.getTipoPredio()
        );

        return predioRepository.save(predioExistente);
    }

    @Transactional
    public void eliminar(Long id) {

        if (!predioRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "No existe un predio con el ID: " + id
            );
        }

        predioRepository.deleteById(id);
    }

    private void validarRelaciones(Predio predio) {

        if (predio.getPropietario() == null
                || predio.getPropietario().getId() == null) {
            throw new IllegalArgumentException(
                    "El predio debe tener un propietario"
            );
        }

        Usuario propietario = usuarioRepository.findById(
                predio.getPropietario().getId()
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "El propietario indicado no existe"
                )
        );

        if (predio.getSector() == null
                || predio.getSector().getId() == null) {
            throw new IllegalArgumentException(
                    "El predio debe pertenecer a un sector"
            );
        }

        Sector sector = sectorRepository.findById(
                predio.getSector().getId()
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "El sector indicado no existe"
                )
        );

        if (predio.getTipoPredio() == null
                || predio.getTipoPredio().getId() == null) {
            throw new IllegalArgumentException(
                    "El predio debe tener un tipo de predio"
            );
        }

        TipoPredio tipoPredio = tipoPredioRepository.findById(
                predio.getTipoPredio().getId()
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "El tipo de predio indicado no existe"
                )
        );

        predio.setPropietario(propietario);
        predio.setSector(sector);
        predio.setTipoPredio(tipoPredio);
    }
}