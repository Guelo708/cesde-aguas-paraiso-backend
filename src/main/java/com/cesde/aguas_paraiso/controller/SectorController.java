package com.cesde.aguas_paraiso.controller;

import com.cesde.aguas_paraiso.dto.SectorRequestDTO;
import com.cesde.aguas_paraiso.dto.SectorResponseDTO;
import com.cesde.aguas_paraiso.service.SectorService;

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
 * relacionadas con los sectores de Aguas Paraíso.
 *
 * Este controlador utiliza DTO para evitar recibir o devolver
 * directamente la entidad Sector.
 *
 * SectorRequestDTO representa los datos de entrada utilizados
 * para registrar o actualizar un sector.
 *
 * SectorResponseDTO representa la información que la API
 * devuelve al cliente.
 *
 * Las reglas de negocio, como la validación de nombres únicos,
 * permanecen en SectorService.
 */
@RestController
@RequestMapping("/api/sectores")
@Tag(
        name = "2. Sectores",
        description = "Operaciones CRUD y consultas de sectores"
)
public class SectorController {

    /**
     * Servicio que contiene la lógica de negocio
     * relacionada con los sectores.
     */
    private final SectorService sectorService;

    /**
     * Constructor utilizado por Spring para inyectar
     * automáticamente una instancia de SectorService.
     *
     * @param sectorService servicio encargado de gestionar sectores
     */
    public SectorController(
            SectorService sectorService
    ) {
        this.sectorService = sectorService;
    }

    /**
     * Obtiene todos los sectores registrados.
     *
     * Ruta:
     * GET /api/sectores
     *
     * @return HTTP 200 con una lista de SectorResponseDTO
     */
    @GetMapping
    @Operation(
            summary = "Listar todos los sectores",
            description = "Consulta todos los sectores registrados en la base de datos"
    )
    public ResponseEntity<List<SectorResponseDTO>> listarTodos() {
        List<SectorResponseDTO> sectores =
                sectorService.listarTodos();

        return ResponseEntity.ok(sectores);
    }

    /**
     * Busca un sector mediante su identificador interno.
     *
     * Ejemplo:
     * GET /api/sectores/1
     *
     * @param id identificador del sector
     * @return HTTP 200 con el sector o HTTP 404 si no existe
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar sector por ID",
            description = "Consulta un sector mediante su identificador interno"
    )
    public ResponseEntity<SectorResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return sectorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    /**
     * Busca un sector por su nombre exacto.
     *
     * La consulta ignora diferencias entre mayúsculas
     * y minúsculas.
     *
     * Ejemplo:
     * GET /api/sectores/nombre/Centro
     *
     * @param nombre nombre exacto del sector
     * @return HTTP 200 con el sector o HTTP 404 si no existe
     */
    @GetMapping("/nombre/{nombre}")
    @Operation(
            summary = "Buscar sector por nombre",
            description = "Consulta un sector por su nombre exacto ignorando mayúsculas y minúsculas"
    )
    public ResponseEntity<SectorResponseDTO> buscarPorNombre(
            @PathVariable String nombre
    ) {
        return sectorService.buscarPorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    /**
     * Busca sectores cuyo nombre contenga el texto recibido.
     *
     * Ejemplo:
     * GET /api/sectores/buscar?nombre=cen
     *
     * @param nombre texto que debe aparecer en el nombre del sector
     * @return HTTP 200 con los sectores encontrados
     */
    @GetMapping("/buscar")
    @Operation(
            summary = "Buscar sectores por nombre parcial",
            description = "Busca sectores cuyo nombre contenga el texto enviado"
    )
    public ResponseEntity<List<SectorResponseDTO>>
    buscarPorNombreParcial(
            @RequestParam String nombre
    ) {
        List<SectorResponseDTO> sectores =
                sectorService.buscarPorNombreParcial(nombre);

        return ResponseEntity.ok(sectores);
    }

    /**
     * Registra un sector nuevo.
     *
     * El cuerpo JSON se convierte en SectorRequestDTO.
     * SectorService construye internamente la entidad Sector,
     * valida que el nombre sea único y guarda el registro.
     *
     * Ruta:
     * POST /api/sectores
     *
     * @param dto datos necesarios para registrar el sector
     * @return HTTP 201 con el SectorResponseDTO creado
     */
    @PostMapping
    @Operation(
            summary = "Registrar un sector",
            description = "Crea un sector validando que el nombre no esté registrado"
    )
    public ResponseEntity<SectorResponseDTO> registrar(
            @RequestBody SectorRequestDTO dto
    ) {
        SectorResponseDTO sectorRegistrado =
                sectorService.registrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sectorRegistrado);
    }

    /**
     * Actualiza un sector existente.
     *
     * El ID se recibe en la URL y los nuevos datos
     * llegan mediante SectorRequestDTO.
     *
     * Ruta:
     * PUT /api/sectores/{id}
     *
     * @param id identificador del sector que se actualizará
     * @param dto nuevos datos del sector
     * @return HTTP 200 con el SectorResponseDTO actualizado
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un sector",
            description = "Actualiza el nombre y la descripción de un sector existente"
    )
    public ResponseEntity<SectorResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody SectorRequestDTO dto
    ) {
        SectorResponseDTO sectorActualizado =
                sectorService.actualizar(id, dto);

        return ResponseEntity.ok(sectorActualizado);
    }

    /**
     * Elimina un sector mediante su identificador.
     *
     * SectorService comprueba primero que exista.
     *
     * Ruta:
     * DELETE /api/sectores/{id}
     *
     * @param id identificador del sector que se eliminará
     * @return HTTP 204 cuando la eliminación sea correcta
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un sector",
            description = "Elimina un sector mediante su identificador interno"
    )
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {
        sectorService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
