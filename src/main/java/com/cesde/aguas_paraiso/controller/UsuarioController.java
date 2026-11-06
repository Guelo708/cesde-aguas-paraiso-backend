package com.cesde.aguas_paraiso.controller;

import com.cesde.aguas_paraiso.dto.UsuarioRequestDTO;
import com.cesde.aguas_paraiso.dto.UsuarioResponseDTO;
import com.cesde.aguas_paraiso.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(
        name = "1. Usuarios",
        description = "Operaciones CRUD y consultas personalizadas de usuarios"
)
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(
            UsuarioService usuarioService
    ) {
        this.usuarioService = usuarioService;
    }

    /*
     * Lista todos los usuarios.
     */
    @GetMapping
    @Operation(
            summary = "Listar todos los usuarios",
            description = "Consulta todos los usuarios registrados"
    )
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        List<UsuarioResponseDTO> usuarios =
                usuarioService.listarTodos();

        return ResponseEntity.ok(usuarios);
    }

    /*
     * Busca un usuario mediante su ID interno.
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar usuario por ID",
            description = "Consulta un usuario mediante su identificador"
    )
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    /*
     * Busca un usuario mediante su número de cédula.
     */
    @GetMapping("/cedula/{cedula}")
    @Operation(
            summary = "Buscar usuario por cédula",
            description = "Consulta un usuario utilizando su número de cédula"
    )
    public ResponseEntity<UsuarioResponseDTO> buscarPorCedula(
            @PathVariable String cedula
    ) {
        return usuarioService.buscarPorCedula(cedula)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    /*
     * Busca un usuario mediante su correo electrónico.
     */
    @GetMapping("/correo/{correoElectronico}")
    @Operation(
            summary = "Buscar usuario por correo electrónico",
            description = "Consulta un usuario utilizando su correo electrónico"
    )
    public ResponseEntity<UsuarioResponseDTO>
    buscarPorCorreoElectronico(
            @PathVariable String correoElectronico
    ) {
        return usuarioService
                .buscarPorCorreoElectronico(
                        correoElectronico
                )
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    /*
     * Registra un usuario utilizando UsuarioRequestDTO.
     */
    @PostMapping
    @Operation(
            summary = "Registrar un usuario",
            description = "Crea un usuario validando que la cédula y el correo no estén registrados"
    )
    public ResponseEntity<UsuarioResponseDTO> registrar(
            @RequestBody UsuarioRequestDTO dto
    ) {
        UsuarioResponseDTO usuarioRegistrado =
                usuarioService.registrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioRegistrado);
    }

    /*
     * Actualiza un usuario utilizando UsuarioRequestDTO.
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un usuario",
            description = "Actualiza los datos y valida la cédula y el correo electrónico"
    )
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody UsuarioRequestDTO dto
    ) {
        UsuarioResponseDTO usuarioActualizado =
                usuarioService.actualizar(id, dto);

        return ResponseEntity.ok(usuarioActualizado);
    }

    /*
     * Elimina un usuario por su ID.
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un usuario",
            description = "Elimina un usuario mediante su identificador interno"
    )
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {
        usuarioService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}