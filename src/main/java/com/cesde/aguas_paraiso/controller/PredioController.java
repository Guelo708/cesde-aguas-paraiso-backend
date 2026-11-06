package com.cesde.aguas_paraiso.controller;

import com.cesde.aguas_paraiso.dto.PredioRequestDTO;
import com.cesde.aguas_paraiso.dto.PredioResponseDTO;
import com.cesde.aguas_paraiso.service.PredioService;

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

/**
 * Controlador REST encargado de recibir las solicitudes HTTP
 * relacionadas con los predios de Aguas Paraíso.
 *
 * Este controlador utiliza DTO para evitar recibir o devolver
 * directamente la entidad JPA Predio.
 *
 * PredioRequestDTO representa los datos de entrada utilizados
 * para registrar o actualizar un predio. Las relaciones se reciben
 * mediante propietarioId, sectorId y tipoPredioId.
 *
 * PredioResponseDTO representa la información que la API devuelve,
 * incluyendo datos resumidos del propietario, sector, tipo de predio,
 * ubicación y fechas de auditoría.
 *
 * Las reglas de negocio, la búsqueda de relaciones y la conversión
 * entre entidades y DTO permanecen en PredioService.
 */
@RestController
@RequestMapping("/api/predios")
@Tag(
        name = "4. Predios",
        description = "Operaciones CRUD y consultas de predios"
)
public class PredioController {

    /**
     * Servicio que contiene la lógica de negocio
     * relacionada con los predios.
     */
    private final PredioService predioService;

    /**
     * Constructor utilizado por Spring para inyectar
     * automáticamente una instancia de PredioService.
     *
     * @param predioService servicio encargado de gestionar predios
     */
    public PredioController(
            PredioService predioService
    ) {
        this.predioService = predioService;
    }

    /**
     * Obtiene todos los predios registrados.
     *
     * Ruta:
     * GET /api/predios
     *
     * @return HTTP 200 con una lista de PredioResponseDTO
     */
    @GetMapping
    @Operation(
            summary = "Listar todos los predios",
            description = "Consulta todos los predios registrados"
    )
    public ResponseEntity<List<PredioResponseDTO>> listarTodos() {
        List<PredioResponseDTO> predios =
                predioService.listarTodos();

        return ResponseEntity.ok(predios);
    }

    /**
     * Busca un predio mediante su identificador interno.
     *
     * Ejemplo:
     * GET /api/predios/1
     *
     * Si el servicio encuentra el registro, se devuelve HTTP 200.
     * Si el Optional está vacío, se devuelve HTTP 404.
     *
     * @param id identificador interno del predio
     * @return HTTP 200 con el predio o HTTP 404 si no existe
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar predio por ID",
            description = "Consulta un predio mediante su identificador"
    )
    public ResponseEntity<PredioResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return predioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    /**
     * Busca un predio mediante su código único.
     *
     * Ejemplo:
     * GET /api/predios/codigo/AP-0001
     *
     * @param codigoPredio código asignado al predio
     * @return HTTP 200 con el predio o HTTP 404 si no existe
     */
    @GetMapping("/codigo/{codigoPredio}")
    @Operation(
            summary = "Buscar predio por código",
            description = "Consulta un predio mediante su código único"
    )
    public ResponseEntity<PredioResponseDTO> buscarPorCodigo(
            @PathVariable String codigoPredio
    ) {
        return predioService.buscarPorCodigo(codigoPredio)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    /**
     * Consulta los predios asociados a un propietario.
     *
     * Ruta:
     * GET /api/predios/propietario/{usuarioId}
     *
     * El parámetro usuarioId corresponde al identificador interno
     * del usuario propietario.
     *
     * @param usuarioId identificador del propietario
     * @return HTTP 200 con los predios del propietario
     */
    @GetMapping("/propietario/{usuarioId}")
    @Operation(
            summary = "Buscar predios por propietario",
            description = "Consulta los predios asociados al ID de un usuario propietario"
    )
    public ResponseEntity<List<PredioResponseDTO>>
    buscarPorPropietario(
            @PathVariable Long usuarioId
    ) {
        List<PredioResponseDTO> predios =
                predioService.buscarPorPropietario(usuarioId);

        return ResponseEntity.ok(predios);
    }

    /**
     * Consulta los predios asociados a un sector.
     *
     * Ruta:
     * GET /api/predios/sector/{sectorId}
     *
     * @param sectorId identificador del sector
     * @return HTTP 200 con los predios del sector
     */
    @GetMapping("/sector/{sectorId}")
    @Operation(
            summary = "Buscar predios por sector",
            description = "Consulta los predios asociados al ID de un sector"
    )
    public ResponseEntity<List<PredioResponseDTO>> buscarPorSector(
            @PathVariable Long sectorId
    ) {
        List<PredioResponseDTO> predios =
                predioService.buscarPorSector(sectorId);

        return ResponseEntity.ok(predios);
    }

    /**
     * Consulta los predios asociados a un tipo de predio.
     *
     * Ruta:
     * GET /api/predios/tipo-predio/{tipoPredioId}
     *
     * @param tipoPredioId identificador del tipo de predio
     * @return HTTP 200 con los predios de esa clasificación
     */
    @GetMapping("/tipo-predio/{tipoPredioId}")
    @Operation(
            summary = "Buscar predios por tipo de predio",
            description = "Consulta los predios asociados al ID de un tipo de predio"
    )
    public ResponseEntity<List<PredioResponseDTO>>
    buscarPorTipoPredio(
            @PathVariable Long tipoPredioId
    ) {
        List<PredioResponseDTO> predios =
                predioService.buscarPorTipoPredio(tipoPredioId);

        return ResponseEntity.ok(predios);
    }

    /**
     * Registra un nuevo predio.
     *
     * Ruta:
     * POST /api/predios
     *
     * El cuerpo JSON se transforma en PredioRequestDTO.
     * En lugar de recibir objetos relacionados incompletos,
     * el DTO recibe los identificadores:
     *
     * - propietarioId.
     * - sectorId.
     * - tipoPredioId.
     *
     * Los campos de Ubicacion también se reciben de forma plana.
     * PredioService busca las entidades relacionadas, construye
     * Ubicacion y guarda la entidad Predio.
     *
     * @param dto datos necesarios para registrar el predio
     * @return HTTP 201 con el PredioResponseDTO creado
     */
    @PostMapping
    @Operation(
            summary = "Registrar un predio",
            description = "Crea un predio y valida su código, propietario, sector y tipo de predio"
    )
    public ResponseEntity<PredioResponseDTO> registrar(
            @RequestBody PredioRequestDTO dto
    ) {
        PredioResponseDTO predioRegistrado =
                predioService.registrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(predioRegistrado);
    }

    /**
     * Actualiza un predio existente.
     *
     * Ruta:
     * PUT /api/predios/{id}
     *
     * El ID del registro se recibe desde la URL.
     * Los nuevos datos se reciben mediante PredioRequestDTO.
     *
     * PredioService comprueba:
     *
     * - Que el predio exista.
     * - Que el código no pertenezca a otro predio.
     * - Que el propietario exista.
     * - Que el sector exista.
     * - Que el tipo de predio exista.
     *
     * @param id identificador del predio que se actualizará
     * @param dto nuevos datos del predio
     * @return HTTP 200 con el PredioResponseDTO actualizado
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un predio",
            description = "Actualiza el código, la ubicación y las relaciones de un predio"
    )
    public ResponseEntity<PredioResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody PredioRequestDTO dto
    ) {
        PredioResponseDTO predioActualizado =
                predioService.actualizar(id, dto);

        return ResponseEntity.ok(predioActualizado);
    }

    /**
     * Elimina un predio mediante su identificador.
     *
     * Ruta:
     * DELETE /api/predios/{id}
     *
     * PredioService comprueba previamente que el registro exista.
     * Cuando la eliminación es correcta, no se devuelve cuerpo.
     *
     * @param id identificador del predio que se eliminará
     * @return HTTP 204 cuando la eliminación sea correcta
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un predio",
            description = "Elimina un predio mediante su identificador"
    )
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {
        predioService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
