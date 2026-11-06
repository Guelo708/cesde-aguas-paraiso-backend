package com.cesde.aguas_paraiso.controller;

import com.cesde.aguas_paraiso.dto.FacturaRequestDTO;
import com.cesde.aguas_paraiso.dto.FacturaResponseDTO;
import com.cesde.aguas_paraiso.model.enums.EstadoFactura;
import com.cesde.aguas_paraiso.service.FacturaService;

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
 * relacionadas con las facturas de Aguas Paraíso.
 *
 * Este controlador utiliza FacturaRequestDTO para la generación
 * individual y FacturaResponseDTO para las respuestas públicas.
 *
 * Las reglas de negocio permanecen en FacturaService:
 *
 * - Validación del periodo.
 * - Prevención de facturas duplicadas.
 * - Búsqueda de la tarifa vigente.
 * - Cálculo del valor de la factura.
 * - Copia histórica de los datos del propietario.
 * - Actualización de facturas vencidas.
 */
@RestController
@RequestMapping("/api/facturas")
@Tag(
        name = "6. Facturas",
        description = "Generación, consultas y administración de facturas"
)
public class FacturaController {

    private final FacturaService facturaService;

    /**
     * Constructor utilizado por Spring para inyectar FacturaService.
     */
    public FacturaController(
            FacturaService facturaService
    ) {
        this.facturaService = facturaService;
    }

    /**
     * Lista todas las facturas registradas.
     *
     * Ruta:
     * GET /api/facturas
     */
    @GetMapping
    @Operation(
            summary = "Listar todas las facturas",
            description = "Consulta todas las facturas registradas"
    )
    public ResponseEntity<List<FacturaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(
                facturaService.listarTodas()
        );
    }

    /**
     * Busca una factura por su identificador interno.
     *
     * Ruta:
     * GET /api/facturas/{id}
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar factura por ID",
            description = "Consulta una factura mediante su identificador"
    )
    public ResponseEntity<FacturaResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return facturaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    /**
     * Busca una factura mediante predio, mes y año.
     *
     * Ejemplo:
     * GET /api/facturas/predio/1/periodo?mes=11&anio=2026
     */
    @GetMapping("/predio/{predioId}/periodo")
    @Operation(
            summary = "Buscar factura por predio y periodo",
            description = "Consulta una factura usando el predio, mes y año"
    )
    public ResponseEntity<FacturaResponseDTO> buscarPorPredioYPeriodo(
            @PathVariable Long predioId,
            @RequestParam Integer mes,
            @RequestParam Integer anio
    ) {
        return facturaService
                .buscarPorPredioYPeriodo(predioId, mes, anio)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    /**
     * Lista el historial de facturas de un predio.
     *
     * Ruta:
     * GET /api/facturas/predio/{predioId}
     */
    @GetMapping("/predio/{predioId}")
    @Operation(
            summary = "Buscar facturas por predio",
            description = "Consulta el historial de facturas de un predio"
    )
    public ResponseEntity<List<FacturaResponseDTO>> buscarPorPredio(
            @PathVariable Long predioId
    ) {
        return ResponseEntity.ok(
                facturaService.buscarPorPredio(predioId)
        );
    }

    /**
     * Lista las facturas asociadas a los predios de un usuario.
     *
     * Ruta:
     * GET /api/facturas/usuario/{usuarioId}
     */
    @GetMapping("/usuario/{usuarioId}")
    @Operation(
            summary = "Buscar facturas por usuario",
            description = "Consulta las facturas asociadas a los predios de un usuario"
    )
    public ResponseEntity<List<FacturaResponseDTO>> buscarPorUsuario(
            @PathVariable Long usuarioId
    ) {
        return ResponseEntity.ok(
                facturaService.buscarPorUsuario(usuarioId)
        );
    }

    /**
     * Lista el historial de facturas mediante la cédula histórica
     * almacenada en las facturas.
     *
     * Ruta:
     * GET /api/facturas/cedula/{cedula}
     */
    @GetMapping("/cedula/{cedula}")
    @Operation(
            summary = "Buscar facturas por cédula",
            description = "Consulta el historial de facturas usando la cédula del propietario"
    )
    public ResponseEntity<List<FacturaResponseDTO>> buscarPorCedula(
            @PathVariable String cedula
    ) {
        return ResponseEntity.ok(
                facturaService.buscarPorCedulaPropietario(cedula)
        );
    }

    /**
     * Lista las facturas pendientes, parciales y vencidas
     * asociadas a una cédula.
     */
    @GetMapping("/cedula/{cedula}/por-pagar")
    @Operation(
            summary = "Buscar facturas por pagar",
            description = "Consulta facturas pendientes, parciales y vencidas por cédula"
    )
    public ResponseEntity<List<FacturaResponseDTO>>
    buscarPorPagarPorCedula(
            @PathVariable String cedula
    ) {
        return ResponseEntity.ok(
                facturaService.buscarFacturasPorPagarPorCedula(
                        cedula
                )
        );
    }

    /**
     * Filtra las facturas de un usuario por estado.
     */
    @GetMapping("/usuario/{usuarioId}/estado/{estado}")
    @Operation(
            summary = "Buscar facturas por usuario y estado",
            description = "Consulta las facturas de un usuario filtradas por estado"
    )
    public ResponseEntity<List<FacturaResponseDTO>>
    buscarPorUsuarioYEstado(
            @PathVariable Long usuarioId,
            @PathVariable EstadoFactura estado
    ) {
        return ResponseEntity.ok(
                facturaService.buscarPorUsuarioYEstado(
                        usuarioId,
                        estado
                )
        );
    }

    /**
     * Filtra las facturas de una cédula por estado.
     */
    @GetMapping("/cedula/{cedula}/estado/{estado}")
    @Operation(
            summary = "Buscar facturas por cédula y estado",
            description = "Consulta las facturas de una cédula filtradas por estado"
    )
    public ResponseEntity<List<FacturaResponseDTO>>
    buscarPorCedulaYEstado(
            @PathVariable String cedula,
            @PathVariable EstadoFactura estado
    ) {
        return ResponseEntity.ok(
                facturaService.buscarPorCedulaYEstado(
                        cedula,
                        estado
                )
        );
    }

    /**
     * Cuenta las facturas de un usuario para un estado determinado.
     */
    @GetMapping("/usuario/{usuarioId}/estado/{estado}/cantidad")
    @Operation(
            summary = "Contar facturas por usuario y estado",
            description = "Cuenta las facturas de un usuario para un estado determinado"
    )
    public ResponseEntity<Long> contarPorUsuarioYEstado(
            @PathVariable Long usuarioId,
            @PathVariable EstadoFactura estado
    ) {
        return ResponseEntity.ok(
                facturaService.contarPorUsuarioYEstado(
                        usuarioId,
                        estado
                )
        );
    }

    /**
     * Cuenta las facturas de una cédula para un estado determinado.
     */
    @GetMapping("/cedula/{cedula}/estado/{estado}/cantidad")
    @Operation(
            summary = "Contar facturas por cédula y estado",
            description = "Cuenta las facturas de una cédula para un estado determinado"
    )
    public ResponseEntity<Long> contarPorCedulaYEstado(
            @PathVariable String cedula,
            @PathVariable EstadoFactura estado
    ) {
        return ResponseEntity.ok(
                facturaService.contarPorCedulaYEstado(
                        cedula,
                        estado
                )
        );
    }

    /**
     * Busca facturas por cédula dentro de un rango de fechas
     * de generación. Las fechas usan el formato AAAA-MM-DD.
     */
    @GetMapping("/cedula/{cedula}/rango")
    @Operation(
            summary = "Buscar facturas por cédula y rango",
            description = "Consulta facturas por cédula y rango de fechas de generación"
    )
    public ResponseEntity<List<FacturaResponseDTO>>
    buscarPorCedulaYRango(
            @PathVariable String cedula,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaInicial,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaFinal
    ) {
        return ResponseEntity.ok(
                facturaService.buscarPorCedulaYRangoDeFechas(
                        cedula,
                        fechaInicial,
                        fechaFinal
                )
        );
    }

    /**
     * Busca facturas aplicando cédula, estado y rango de fechas.
     */
    @GetMapping("/cedula/{cedula}/estado/{estado}/rango")
    @Operation(
            summary = "Buscar facturas por cédula, estado y rango",
            description = "Consulta facturas aplicando cédula, estado y rango de fechas"
    )
    public ResponseEntity<List<FacturaResponseDTO>>
    buscarPorCedulaEstadoYRango(
            @PathVariable String cedula,
            @PathVariable EstadoFactura estado,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaInicial,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaFinal
    ) {
        return ResponseEntity.ok(
                facturaService
                        .buscarPorCedulaEstadoYRangoDeFechas(
                                cedula,
                                estado,
                                fechaInicial,
                                fechaFinal
                        )
        );
    }

    /**
     * Genera una factura individual mediante FacturaRequestDTO.
     *
     * Ruta:
     * POST /api/facturas/generar
     *
     * Cuerpo esperado:
     * {
     *   "predioId": 1,
     *   "mes": 11,
     *   "anio": 2026
     * }
     */
    @PostMapping("/generar")
    @Operation(
            summary = "Generar una factura",
            description = "Genera la factura de un predio para un mes y año"
    )
    public ResponseEntity<FacturaResponseDTO> generarFactura(
            @RequestBody FacturaRequestDTO dto
    ) {
        FacturaResponseDTO factura =
                facturaService.generarFactura(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(factura);
    }

    /**
     * Genera las facturas mensuales de todos los predios que aún
     * no tengan factura para el mes y año indicados.
     *
     * Se mantienen mes y año como parámetros porque la operación
     * es masiva y no corresponde a un único predio.
     */
    @PostMapping("/generar-mensuales")
    @Operation(
            summary = "Generar facturas mensuales",
            description = "Genera las facturas de todos los predios para un mes y año"
    )
    public ResponseEntity<List<FacturaResponseDTO>>
    generarFacturasMensuales(
            @RequestParam Integer mes,
            @RequestParam Integer anio
    ) {
        List<FacturaResponseDTO> facturas =
                facturaService.generarFacturasMensuales(
                        mes,
                        anio
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(facturas);
    }

    /**
     * Marca como vencidas las facturas no pagadas cuya fecha límite
     * ya fue superada.
     *
     * @return cantidad de facturas que cambiaron de estado
     */
    @PutMapping("/actualizar-vencidas")
    @Operation(
            summary = "Actualizar facturas vencidas",
            description = "Marca como vencidas las facturas no pagadas que superaron su fecha límite"
    )
    public ResponseEntity<Integer> actualizarFacturasVencidas() {
        return ResponseEntity.ok(
                facturaService.actualizarFacturasVencidas()
        );
    }

    /**
     * Elimina una factura mediante su identificador.
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar una factura",
            description = "Elimina una factura mediante su identificador"
    )
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {
        facturaService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
