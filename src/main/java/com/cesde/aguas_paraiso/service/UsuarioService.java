package com.cesde.aguas_paraiso.service;

import com.cesde.aguas_paraiso.dto.UsuarioRequestDTO;
import com.cesde.aguas_paraiso.dto.UsuarioResponseDTO;
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

    public UsuarioService(
            UsuarioRepository usuarioRepository
    ) {
        this.usuarioRepository = usuarioRepository;
    }

    /*
     * Consulta todos los usuarios y convierte cada entidad
     * Usuario en un UsuarioResponseDTO.
     */
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /*
     * Busca un usuario por su ID.
     *
     * Si existe, transforma la entidad encontrada
     * en UsuarioResponseDTO.
     */
    public Optional<UsuarioResponseDTO> buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(this::convertirADTO);
    }

    /*
     * Busca un usuario por su cédula.
     */
    public Optional<UsuarioResponseDTO> buscarPorCedula(
            String cedula
    ) {
        return usuarioRepository.findByCedula(cedula)
                .map(this::convertirADTO);
    }

    /*
     * Busca un usuario por su correo electrónico.
     */
    public Optional<UsuarioResponseDTO> buscarPorCorreoElectronico(
            String correoElectronico
    ) {
        return usuarioRepository
                .findByCorreoElectronico(correoElectronico)
                .map(this::convertirADTO);
    }

    /*
     * Registra un usuario a partir de los datos contenidos
     * en UsuarioRequestDTO.
     */
    @Transactional
    public UsuarioResponseDTO registrar(
            UsuarioRequestDTO dto
    ) {
        if (usuarioRepository.existsByCedula(dto.cedula())) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario registrado con esta cédula"
            );
        }

        String correoElectronico = normalizarCorreo(
                dto.correoElectronico()
        );

        if (correoElectronico != null
                && usuarioRepository.existsByCorreoElectronico(
                        correoElectronico
                )) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario registrado con este correo electrónico"
            );
        }

        Usuario usuario = Usuario.builder()
                .cedula(dto.cedula())
                .primerNombre(dto.primerNombre())
                .segundoNombre(dto.segundoNombre())
                .primerApellido(dto.primerApellido())
                .segundoApellido(dto.segundoApellido())
                .celular(dto.celular())
                .correoElectronico(correoElectronico)
                .build();

        Usuario usuarioGuardado =
                usuarioRepository.save(usuario);

        return convertirADTO(usuarioGuardado);
    }

    /*
     * Actualiza un usuario existente utilizando
     * la información recibida en UsuarioRequestDTO.
     */
    @Transactional
    public UsuarioResponseDTO actualizar(
            Long id,
            UsuarioRequestDTO dto
    ) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe un usuario con el ID: " + id
                        )
                );

        Optional<Usuario> usuarioConCedula =
                usuarioRepository.findByCedula(dto.cedula());

        if (usuarioConCedula.isPresent()
                && !usuarioConCedula.get()
                .getId()
                .equals(id)) {
            throw new IllegalArgumentException(
                    "La cédula ya pertenece a otro usuario"
            );
        }

        String correoActualizado = normalizarCorreo(
                dto.correoElectronico()
        );

        if (correoActualizado != null) {
            Optional<Usuario> usuarioConCorreo =
                    usuarioRepository.findByCorreoElectronico(
                            correoActualizado
                    );

            if (usuarioConCorreo.isPresent()
                    && !usuarioConCorreo.get()
                    .getId()
                    .equals(id)) {
                throw new IllegalArgumentException(
                        "El correo electrónico ya pertenece a otro usuario"
                );
            }
        }

        usuarioExistente.setCedula(dto.cedula());
        usuarioExistente.setPrimerNombre(dto.primerNombre());
        usuarioExistente.setSegundoNombre(dto.segundoNombre());
        usuarioExistente.setPrimerApellido(dto.primerApellido());
        usuarioExistente.setSegundoApellido(dto.segundoApellido());
        usuarioExistente.setCelular(dto.celular());
        usuarioExistente.setCorreoElectronico(
                correoActualizado
        );

        Usuario usuarioActualizado =
                usuarioRepository.save(usuarioExistente);

        return convertirADTO(usuarioActualizado);
    }

    /*
     * Elimina un usuario después de comprobar
     * que exista en la base de datos.
     */
    @Transactional
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "No existe un usuario con el ID: " + id
            );
        }

        usuarioRepository.deleteById(id);
    }

    /*
     * Convierte una entidad Usuario en un
     * UsuarioResponseDTO.
     */
    public UsuarioResponseDTO convertirADTO(
            Usuario usuario
    ) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getCedula(),
                construirNombreCompleto(usuario),
                usuario.getPrimerNombre(),
                usuario.getSegundoNombre(),
                usuario.getPrimerApellido(),
                usuario.getSegundoApellido(),
                usuario.getCelular(),
                usuario.getCorreoElectronico()
        );
    }

    /*
     * Construye el nombre completo sin agregar
     * espacios innecesarios cuando un segundo nombre
     * o segundo apellido sea nulo o esté vacío.
     */
    private String construirNombreCompleto(
            Usuario usuario
    ) {
        StringBuilder nombreCompleto =
                new StringBuilder();

        agregarParteNombre(
                nombreCompleto,
                usuario.getPrimerNombre()
        );

        agregarParteNombre(
                nombreCompleto,
                usuario.getSegundoNombre()
        );

        agregarParteNombre(
                nombreCompleto,
                usuario.getPrimerApellido()
        );

        agregarParteNombre(
                nombreCompleto,
                usuario.getSegundoApellido()
        );

        return nombreCompleto.toString();
    }

    /*
     * Añade una parte del nombre solamente cuando
     * contiene información válida.
     */
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

    /*
     * Convierte un correo vacío en null y elimina
     * espacios al inicio o al final.
     */
    private String normalizarCorreo(
            String correoElectronico
    ) {
        if (correoElectronico == null
                || correoElectronico.isBlank()) {
            return null;
        }

        return correoElectronico.trim();
    }
}
