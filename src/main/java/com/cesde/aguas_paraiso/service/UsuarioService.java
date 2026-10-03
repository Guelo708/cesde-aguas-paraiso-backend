package com.cesde.aguas_paraiso.service;

import com.cesde.aguas_paraiso.model.entity.Usuario;
import com.cesde.aguas_paraiso.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorCedula(String cedula) {
        return usuarioRepository.findByCedula(cedula);
    }

    public Optional<Usuario> buscarPorCorreoElectronico(
            String correoElectronico
    ) {
        return usuarioRepository.findByCorreoElectronico(
                correoElectronico
        );
    }

    @Transactional
    public Usuario registrar(Usuario usuario) {

        if (usuarioRepository.existsByCedula(usuario.getCedula())) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario registrado con esta cédula"
            );
        }

        if (usuario.getCorreoElectronico() != null
                && !usuario.getCorreoElectronico().isBlank()
                && usuarioRepository.existsByCorreoElectronico(
                        usuario.getCorreoElectronico()
                )) {

            throw new IllegalArgumentException(
                    "Ya existe un usuario registrado con este correo electrónico"
            );
        }

        if (usuario.getCorreoElectronico() != null
                && usuario.getCorreoElectronico().isBlank()) {
            usuario.setCorreoElectronico(null);
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizar(
            Long id,
            Usuario datosActualizados
    ) {

        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe un usuario con el ID: " + id
                        )
                );

        Optional<Usuario> usuarioConCedula =
                usuarioRepository.findByCedula(
                        datosActualizados.getCedula()
                );

        if (usuarioConCedula.isPresent()
                && !usuarioConCedula.get().getId().equals(id)) {

            throw new IllegalArgumentException(
                    "La cédula ya pertenece a otro usuario"
            );
        }

        String correoActualizado =
                datosActualizados.getCorreoElectronico();

        if (correoActualizado != null
                && !correoActualizado.isBlank()) {

            Optional<Usuario> usuarioConCorreo =
                    usuarioRepository.findByCorreoElectronico(
                            correoActualizado
                    );

            if (usuarioConCorreo.isPresent()
                    && !usuarioConCorreo.get().getId().equals(id)) {

                throw new IllegalArgumentException(
                        "El correo electrónico ya pertenece a otro usuario"
                );
            }
        } else {
            correoActualizado = null;
        }

        usuarioExistente.setCedula(
                datosActualizados.getCedula()
        );

        usuarioExistente.setPrimerNombre(
                datosActualizados.getPrimerNombre()
        );

        usuarioExistente.setSegundoNombre(
                datosActualizados.getSegundoNombre()
        );

        usuarioExistente.setPrimerApellido(
                datosActualizados.getPrimerApellido()
        );

        usuarioExistente.setSegundoApellido(
                datosActualizados.getSegundoApellido()
        );

        usuarioExistente.setCelular(
                datosActualizados.getCelular()
        );

        usuarioExistente.setCorreoElectronico(
                correoActualizado
        );

        return usuarioRepository.save(usuarioExistente);
    }

    @Transactional
    public void eliminar(Long id) {

        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "No existe un usuario con el ID: " + id
            );
        }

        usuarioRepository.deleteById(id);
    }
}
