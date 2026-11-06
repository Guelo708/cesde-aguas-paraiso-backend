package com.cesde.aguas_paraiso.controller;

import com.cesde.aguas_paraiso.dto.TarifaRequestDTO;
import com.cesde.aguas_paraiso.dto.TarifaResponseDTO;
import com.cesde.aguas_paraiso.service.TarifaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST encargado de recibir las solicitudes HTTP
 * relacionadas con las tarifas de Aguas Paraíso.
 *
 * Este controlador utiliza DTO para evitar recibir o devolver
 * directamente la entidad JPA Tarifa.
 *
 * TarifaRequestDTO representa los datos de entrada utilizados
 * para registrar o actualizar una tarifa.
 *
 * TarifaResponseDTO representa la información que la API
 * devuelve al cliente, incluyendo los datos del tipo de predio
 * mediante TipoPredioResponseDTO.
 *
 * Las reglas de negocio relacionadas con valores, fechas,
 * vigencias y tipos de predio permanecen en TarifaService.
 */
@RestController
@RequestMapping("/api/tarifas")
@Tag(
        name = "5. Tarifas",
        description = "Operaciones CRUD y consultas del historial de tarifas"
)
public class TarifaController {

    /**
     * Servicio que contiene la lógica de negocio
     * relacionada con las tarifas.
     */
    private final TarifaService tarifaService;

    /**
     * Constructor utilizado por Spring para inyectar
     * automáticamente una instancia de TarifaService.
     *
     * @param tarifaService servicio encargado de gestionar tarifas
     */
    public TarifaController(
            TarifaService tarifaService
    ) {
        this.tarifaService = tarifaService;
    }

    /**
     * Obtiene todas las tarifas registradas.
     *
     * Ruta:
     * GET /api/tarifas
     *
     * El controlador recibe del servicio una lista de DTO,
     * no una lista de entidades Tarifa.
     *
     * @return HTTP 200 con una lista de TarifaResponseDTO
     */
    @GetMapping
    @Operation(
            summary = "Listar todas las tarifas",
            description = "Consulta todas las tarifas registradas"
    )
    public ResponseEntity<List<TarifaResponseDTO>> listarTodas() {
        List<TarifaResponseDTO> tarifas =
                tarifaService.listarTodas();

        return ResponseEntity.ok(tarifas);
    }

    /**
     * Busca una tarifa mediante su identificador interno.
     *
     * Ejemplo:
     * GET /api/tarifas/1
     *
     * Si el servicio devuelve un Optional con información,
     * se responde con HTTP 200. Si está vacío, se responde
     * con HTTP 404.
     *
     * @param id identificador de la tarifa
     * @return HTTP 200 con la tarifa o HTTP 404 si no existe
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar tarifa por ID",
            description = "Consulta una tarifa mediante su identificador"
    )
    public ResponseEntity<TarifaResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return tarifaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    /**
     * Consulta el historial de tarifas de un tipo de predio.
     *
     * Ruta:
     * GET /api/tarifas/tipo-predio/{tipoPredioId}/historial
     *
     * El historial se devuelve ordenado desde la tarifa
     * más reciente, según la consulta del repositorio.
     *
     * @param tipoPredioId identificador del tipo de predio
     * @return HTTP 200 con el historial convertido a DTO
     */
    @GetMapping("/tipo-predio/{tipoPredioId}/historial")
    @Operation(
            summary = "Consultar historial de tarifas",
            description = "Consulta el historial de tarifas de un tipo de predio"
    )
    public ResponseEntity<List<TarifaResponseDTO>>
    buscarHistorialPorTipoPredio(
            @PathVariable Long tipoPredioId
    ) {
        List<TarifaResponseDTO> tarifas =
                tarifaService.buscarHistorialPorTipoPredio(
                        tipoPredioId
                );

        return ResponseEntity.ok(tarifas);
    }

    /**
     * Busca la tarifa vigente de un tipo de predio
     * para una fecha determinada.
     *
     * Ruta de ejemplo:
     * GET /api/tarifas/tipo-predio/1/vigente?fecha=2026-11-01
     *
     * @DateTimeFormat indica que la fecha debe recibirse
     * con formato ISO: AAAA-MM-DD.
     *
     * @param tipoPredioId identificador del tipo de predio
     * @param fecha fecha para la cual se consulta la vigencia
     * @return HTTP 200 con la tarifa o HTTP 404 si no existe
     */
    @GetMapping("/tipo-predio/{tipoPredioId}/vigente")
    @Operation(
            summary = "Buscar tarifa vigente",
            description = "Consulta la tarifa vigente de un tipo de predio para una fecha"
    )
    public ResponseEntity<TarifaResponseDTO> buscarTarifaVigente(
            @PathVariable Long tipoPredioId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha
    ) {
        return tarifaService
                .buscarTarifaVigente(tipoPredioId, fecha)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    /**
     * Registra una nueva tarifa.
     *
     * Ruta:
     * POST /api/tarifas
     *
     * El cuerpo JSON se transforma en TarifaRequestDTO.
     * En lugar de recibir una entidad TipoPredio incompleta,
     * el DTO recibe solamente tipoPredioId.
     *
     * TarifaService busca la entidad relacionada, valida
     * el valor y las fechas, ajusta la vigencia anterior
     * cuando corresponde y guarda la nueva tarifa.
     *
     * @param dto datos necesarios para registrar la tarifa
     * @return HTTP 201 con la tarifa creada como ResponseDTO
     */
    @PostMapping
    @Operation(
            summary = "Registrar una tarifa",
            description = "Crea una tarifa y actualiza la vigencia de la tarifa anterior"
    )
    public ResponseEntity<TarifaResponseDTO> registrar(
            @RequestBody TarifaRequestDTO dto
    ) {
        TarifaResponseDTO tarifaRegistrada =
                tarifaService.registrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tarifaRegistrada);
    }

    /**
     * Actualiza una tarifa existente.
     *
     * Ruta:
     * PUT /api/tarifas/{id}
     *
     * El ID de la tarifa se recibe desde la URL.
     * Los nuevos datos se reciben en TarifaRequestDTO.
     *
     * El servicio comprueba que la tarifa y el tipo de predio
     * existan, valida el valor y las fechas, y finalmente
     * devuelve TarifaResponseDTO.
     *
     * @param id identificador de la tarifa que se actualizará
     * @param dto nuevos datos de la tarifa
     * @return HTTP 200 con la tarifa actualizada
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar una tarifa",
            description = "Actualiza el tipo de predio, el valor y las fechas de una tarifa"
    )
    public ResponseEntity<TarifaResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody TarifaRequestDTO dto
    ) {
        TarifaResponseDTO tarifaActualizada =
                tarifaService.actualizar(id, dto);

        return ResponseEntity.ok(tarifaActualizada);
    }

    /**
     * Elimina una tarifa mediante su identificador.
     *
     * Ruta:
     * DELETE /api/tarifas/{id}
     *
     * TarifaService comprueba previamente que la tarifa exista.
     * Cuando la eliminación es correcta, no se devuelve cuerpo.
     *
     * @param id identificador de la tarifa que se eliminará
     * @return HTTP 204 cuando la eliminación sea correcta
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar una tarifa",
            description = "Elimina una tarifa mediante su identificador"
    )
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {
        tarifaService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
