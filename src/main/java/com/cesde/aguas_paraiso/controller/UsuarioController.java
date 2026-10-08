package com.cesde.aguas_paraiso.controller;

import com.cesde.aguas_paraiso.model.entity.Usuario;
import com.cesde.aguas_paraiso.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de recibir las solicitudes HTTP
 * relacionadas con los usuarios del Acueducto Aguas Paraíso.
 *
 * Este controlador permite realizar operaciones CRUD:
 *
 * - Listar usuarios.
 * - Buscar usuarios por ID.
 * - Buscar usuarios por cédula.
 * - Buscar usuarios por correo electrónico.
 * - Registrar usuarios.
 * - Actualizar usuarios.
 * - Eliminar usuarios.
 *
 * El controlador no contiene las reglas del negocio.
 * Las validaciones de cédula, correo electrónico y existencia
 * del usuario se delegan a UsuarioService.
 */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "1. Usuarios", description = "Operaciones CRUD y consultas personalizadas de usuarios")
public class UsuarioController {

    /**
     * Servicio que contiene la lógica del negocio
     * relacionada con los usuarios.
     *
     * Se declara final porque la dependencia se asigna
     * una sola vez mediante el constructor.
     */
    private final UsuarioService usuarioService;

    /**
     * Constructor utilizado por Spring para inyectar
     * automáticamente una instancia de UsuarioService.
     *
     * @param usuarioService servicio encargado de administrar
     *                       la lógica de los usuarios
     */
    public UsuarioController(
            UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Obtiene todos los usuarios registrados en el sistema.
     *
     * Ruta:
     * GET /api/usuarios
     *
     * @return respuesta HTTP 200 con la lista de usuarios
     */
    @GetMapping
    @Operation(summary = "Listar todos los usuarios", description = "Consulta todos los usuarios registrados en la base de datos")
    public ResponseEntity<List<Usuario>> listarTodos() {

        List<Usuario> usuarios = usuarioService.listarTodos();

        return ResponseEntity.ok(usuarios);
    }

    /**
     * Busca un usuario utilizando su identificador interno.
     *
     * Ejemplo:
     * GET /api/usuarios/5
     *
     * El ID corresponde al identificador heredado
     * de la clase BaseEntity.
     *
     * @param id identificador interno del usuario
     * @return HTTP 200 si el usuario existe
     *         o HTTP 404 si no fue encontrado
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID", description = "Consulta un usuario mediante el identificador heredado de BaseEntity")
    public ResponseEntity<Usuario> buscarPorId(
            @PathVariable Long id) {

        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Busca un usuario mediante su número de cédula.
     *
     * Ejemplo:
     * GET /api/usuarios/cedula/123456789
     *
     * Esta consulta será especialmente útil para el panel
     * del propietario, porque el usuario conoce su cédula
     * pero no necesariamente conoce el ID interno.
     *
     * @param cedula número de identificación del usuario
     * @return HTTP 200 si el usuario existe
     *         o HTTP 404 si no fue encontrado
     */
    @GetMapping("/cedula/{cedula}")
    @Operation(summary = "Buscar usuario por cédula", description = "Consulta un usuario utilizando su número de cédula")
    public ResponseEntity<Usuario> buscarPorCedula(
            @PathVariable String cedula) {

        return usuarioService.buscarPorCedula(cedula)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Busca un usuario mediante su correo electrónico.
     *
     * Ejemplo:
     * GET /api/usuarios/correo/usuario@correo.com
     *
     * @param correoElectronico correo que se desea consultar
     * @return HTTP 200 si el usuario existe
     *         o HTTP 404 si no fue encontrado
     */
    @GetMapping("/correo/{correoElectronico}")
    @Operation(summary = "Buscar usuario por correo electrónico", description = "Consulta un usuario mediante su correo electrónico")
    public ResponseEntity<Usuario> buscarPorCorreoElectronico(
            @PathVariable String correoElectronico) {

        return usuarioService
                .buscarPorCorreoElectronico(
                        correoElectronico)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Registra un usuario nuevo.
     *
     * Ruta:
     * POST /api/usuarios
     *
     * La anotación @RequestBody convierte el JSON recibido
     * en un objeto de tipo Usuario.
     *
     * UsuarioService valida que la cédula y el correo
     * electrónico no estén registrados previamente.
     *
     * @param usuario datos del usuario que será registrado
     * @return HTTP 201 con la información del usuario creado
     */
    @PostMapping
    @Operation(summary = "Registrar un usuario", description = "Crea un usuario validando que la cédula y el correo no estén registrados")
    public ResponseEntity<Usuario> registrar(
            @RequestBody Usuario usuario) {

        Usuario usuarioRegistrado = usuarioService.registrar(usuario);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioRegistrado);
    }

    /**
     * Actualiza la información de un usuario existente.
     *
     * Ruta:
     * PUT /api/usuarios/{id}
     *
     * El ID se recibe desde la URL.
     * Los datos actualizados se reciben dentro del cuerpo JSON.
     *
     * UsuarioService verifica:
     *
     * - Que el usuario exista.
     * - Que la cédula no pertenezca a otro usuario.
     * - Que el correo no pertenezca a otro usuario.
     *
     * @param id      identificador del usuario que se actualizará
     * @param usuario nuevos datos del usuario
     * @return HTTP 200 con el usuario actualizado
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario", description = "Actualiza los datos y valida la cédula y el correo electrónico")
    public ResponseEntity<Usuario> actualizar(
            @PathVariable Long id,
            @RequestBody Usuario usuario) {

        Usuario usuarioActualizado = usuarioService.actualizar(
                id,
                usuario);

        return ResponseEntity.ok(
                usuarioActualizado);
    }

    /**
     * Elimina un usuario mediante su identificador interno.
     *
     * Ruta:
     * DELETE /api/usuarios/{id}
     *
     * UsuarioService verifica primero que el usuario exista.
     *
     * @param id identificador del usuario que se eliminará
     * @return HTTP 204 cuando la eliminación sea exitosa
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario mediante su identificador interno")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        usuarioService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}