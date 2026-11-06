package com.cesde.aguas_paraiso.controller;

import com.cesde.aguas_paraiso.dto.TipoPredioRequestDTO;
import com.cesde.aguas_paraiso.dto.TipoPredioResponseDTO;
import com.cesde.aguas_paraiso.service.TipoPredioService;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST encargado de recibir las solicitudes HTTP
 * relacionadas con los tipos de predio de Aguas Paraíso.
 *
 * Este controlador utiliza DTO para evitar recibir o devolver
 * directamente la entidad TipoPredio.
 *
 * TipoPredioRequestDTO representa los datos de entrada utilizados
 * para registrar o actualizar un tipo de predio.
 *
 * TipoPredioResponseDTO representa la información que la API
 * devuelve al cliente.
 *
 * Las reglas de negocio, como la validación de nombres únicos,
 * permanecen en TipoPredioService.
 */
@RestController
@RequestMapping("/api/tipos-predio")
@Tag(
        name = "3. Tipos de predio",
        description = "Operaciones CRUD y consultas de tipos de predio"
)
public class TipoPredioController {

    /**
     * Servicio que contiene la lógica de negocio
     * relacionada con los tipos de predio.
     */
    private final TipoPredioService tipoPredioService;

    /**
     * Constructor utilizado por Spring para inyectar
     * automáticamente una instancia de TipoPredioService.
     *
     * @param tipoPredioService servicio encargado de gestionar
     *                          los tipos de predio
     */
    public TipoPredioController(
            TipoPredioService tipoPredioService
    ) {
        this.tipoPredioService = tipoPredioService;
    }

    /**
     * Obtiene todos los tipos de predio registrados.
     *
     * Ruta:
     * GET /api/tipos-predio
     *
     * @return HTTP 200 con una lista de TipoPredioResponseDTO
     */
    @GetMapping
    @Operation(
            summary = "Listar todos los tipos de predio",
            description = "Consulta todos los tipos de predio registrados"
    )
    public ResponseEntity<List<TipoPredioResponseDTO>> listarTodos() {
        List<TipoPredioResponseDTO> tiposPredio =
                tipoPredioService.listarTodos();

        return ResponseEntity.ok(tiposPredio);
    }

    /**
     * Busca un tipo de predio mediante su identificador interno.
     *
     * Ejemplo:
     * GET /api/tipos-predio/1
     *
     * @param id identificador del tipo de predio
     * @return HTTP 200 con el tipo de predio o HTTP 404 si no existe
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar tipo de predio por ID",
            description = "Consulta un tipo de predio mediante su identificador"
    )
    public ResponseEntity<TipoPredioResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return tipoPredioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    /**
     * Busca un tipo de predio por su nombre exacto.
     *
     * La búsqueda ignora diferencias entre mayúsculas
     * y minúsculas.
     *
     * Ejemplo:
     * GET /api/tipos-predio/nombre/Residencial
     *
     * @param nombre nombre exacto del tipo de predio
     * @return HTTP 200 con el tipo de predio o HTTP 404 si no existe
     */
    @GetMapping("/nombre/{nombre}")
    @Operation(
            summary = "Buscar tipo de predio por nombre",
            description = "Consulta por nombre exacto ignorando mayúsculas y minúsculas"
    )
    public ResponseEntity<TipoPredioResponseDTO> buscarPorNombre(
            @PathVariable String nombre
    ) {
        return tipoPredioService.buscarPorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    /**
     * Busca tipos de predio cuyo nombre contenga
     * el texto recibido.
     *
     * Ejemplo:
     * GET /api/tipos-predio/buscar?nombre=res
     *
     * @param nombre texto que debe aparecer en el nombre
     * @return HTTP 200 con las coincidencias encontradas
     */
    @GetMapping("/buscar")
    @Operation(
            summary = "Buscar tipos de predio por nombre parcial",
            description = "Busca tipos de predio cuyo nombre contenga el texto recibido"
    )
    public ResponseEntity<List<TipoPredioResponseDTO>>
    buscarPorNombreParcial(
            @RequestParam String nombre
    ) {
        List<TipoPredioResponseDTO> tiposPredio =
                tipoPredioService.buscarPorNombreParcial(nombre);

        return ResponseEntity.ok(tiposPredio);
    }

    /**
     * Registra un nuevo tipo de predio.
     *
     * El cuerpo JSON se convierte en TipoPredioRequestDTO.
     * TipoPredioService construye internamente la entidad,
     * valida que el nombre sea único y guarda el registro.
     *
     * Ruta:
     * POST /api/tipos-predio
     *
     * @param dto datos necesarios para registrar el tipo de predio
     * @return HTTP 201 con el TipoPredioResponseDTO creado
     */
    @PostMapping
    @Operation(
            summary = "Registrar un tipo de predio",
            description = "Crea un tipo de predio validando que el nombre no esté registrado"
    )
    public ResponseEntity<TipoPredioResponseDTO> registrar(
            @RequestBody TipoPredioRequestDTO dto
    ) {
        TipoPredioResponseDTO tipoPredioRegistrado =
                tipoPredioService.registrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tipoPredioRegistrado);
    }

    /**
     * Actualiza un tipo de predio existente.
     *
     * El ID se recibe en la URL y los nuevos datos
     * llegan mediante TipoPredioRequestDTO.
     *
     * Ruta:
     * PUT /api/tipos-predio/{id}
     *
     * @param id identificador del registro que se actualizará
     * @param dto nuevos datos del tipo de predio
     * @return HTTP 200 con el TipoPredioResponseDTO actualizado
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un tipo de predio",
            description = "Actualiza el nombre y la descripción de un tipo de predio"
    )
    public ResponseEntity<TipoPredioResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody TipoPredioRequestDTO dto
    ) {
        TipoPredioResponseDTO tipoPredioActualizado =
                tipoPredioService.actualizar(id, dto);

        return ResponseEntity.ok(tipoPredioActualizado);
    }

    /**
     * Elimina un tipo de predio mediante su identificador.
     *
     * TipoPredioService comprueba previamente que exista.
     *
     * Ruta:
     * DELETE /api/tipos-predio/{id}
     *
     * @param id identificador del registro que se eliminará
     * @return HTTP 204 cuando la eliminación sea correcta
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un tipo de predio",
            description = "Elimina un tipo de predio mediante su identificador"
    )
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {
        tipoPredioService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
