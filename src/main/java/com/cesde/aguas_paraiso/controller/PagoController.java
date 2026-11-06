package com.cesde.aguas_paraiso.controller;

import com.cesde.aguas_paraiso.dto.PagoRequestDTO;
import com.cesde.aguas_paraiso.dto.PagoResponseDTO;
import com.cesde.aguas_paraiso.service.PagoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST encargado de recibir las solicitudes HTTP
 * relacionadas con los pagos de Aguas Paraíso.
 *
 * Este controlador utiliza:
 *
 * - PagoRequestDTO para recibir los datos de un nuevo pago.
 * - PagoResponseDTO para entregar las respuestas públicas.
 *
 * La lógica financiera permanece en PagoService, incluyendo:
 *
 * - Validación de la factura.
 * - Validación del valor y la fecha del pago.
 * - Control del saldo pendiente.
 * - Actualización del estado de la factura.
 * - Reajuste del estado cuando se elimina un pago.
 */
@RestController
@RequestMapping("/api/pagos")
@Tag(
        name = "7. Pagos",
        description = "Registro, consultas y cálculos relacionados con pagos"
)
public class PagoController {

    /**
     * Servicio que contiene la lógica de negocio
     * relacionada con pagos y saldos.
     */
    private final PagoService pagoService;

    /**
     * Constructor utilizado por Spring para inyectar PagoService.
     *
     * @param pagoService servicio encargado de gestionar pagos
     */
    public PagoController(
            PagoService pagoService
    ) {
        this.pagoService = pagoService;
    }

    /**
     * Lista todos los pagos registrados.
     *
     * Ruta:
     * GET /api/pagos
     *
     * @return HTTP 200 con la lista de PagoResponseDTO
     */
    @GetMapping
    @Operation(
            summary = "Listar todos los pagos",
            description = "Consulta todos los pagos registrados"
    )
    public ResponseEntity<List<PagoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
                pagoService.listarTodos()
        );
    }

    /**
     * Busca un pago mediante su identificador interno.
     *
     * Ruta:
     * GET /api/pagos/{id}
     *
     * @param id identificador del pago
     * @return HTTP 200 con el pago o HTTP 404 si no existe
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar pago por ID",
            description = "Consulta un pago mediante su identificador"
    )
    public ResponseEntity<PagoResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return pagoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    /**
     * Lista los pagos asociados a una factura.
     *
     * Ruta:
     * GET /api/pagos/factura/{facturaId}
     *
     * @param facturaId identificador de la factura
     * @return HTTP 200 con los pagos de la factura
     */
    @GetMapping("/factura/{facturaId}")
    @Operation(
            summary = "Buscar pagos por factura",
            description = "Consulta los pagos asociados a una factura"
    )
    public ResponseEntity<List<PagoResponseDTO>> buscarPorFactura(
            @PathVariable Long facturaId
    ) {
        return ResponseEntity.ok(
                pagoService.buscarPorFactura(facturaId)
        );
    }

    /**
     * Lista los pagos asociados a las facturas
     * pertenecientes a los predios de un usuario.
     *
     * Ruta:
     * GET /api/pagos/usuario/{usuarioId}
     *
     * @param usuarioId identificador del usuario
     * @return HTTP 200 con los pagos asociados al usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    @Operation(
            summary = "Buscar pagos por usuario",
            description = "Consulta los pagos asociados a un usuario"
    )
    public ResponseEntity<List<PagoResponseDTO>> buscarPorUsuario(
            @PathVariable Long usuarioId
    ) {
        return ResponseEntity.ok(
                pagoService.buscarPorUsuario(usuarioId)
        );
    }

    /**
     * Lista los pagos asociados a una cédula.
     *
     * Ruta:
     * GET /api/pagos/cedula/{cedula}
     *
     * @param cedula cédula histórica del propietario
     * @return HTTP 200 con los pagos encontrados
     */
    @GetMapping("/cedula/{cedula}")
    @Operation(
            summary = "Buscar pagos por cédula",
            description = "Consulta los pagos asociados a la cédula de un propietario"
    )
    public ResponseEntity<List<PagoResponseDTO>> buscarPorCedula(
            @PathVariable String cedula
    ) {
        return ResponseEntity.ok(
                pagoService.buscarPorCedula(cedula)
        );
    }

    /**
     * Busca pagos por cédula dentro de un rango de fechas.
     *
     * Las fechas deben enviarse con formato AAAA-MM-DD.
     *
     * Ejemplo:
     * GET /api/pagos/cedula/1037654321/rango
     *     ?fechaInicial=2026-01-01
     *     &fechaFinal=2026-12-31
     *
     * @param cedula cédula del propietario
     * @param fechaInicial fecha inicial inclusiva
     * @param fechaFinal fecha final inclusiva
     * @return HTTP 200 con los pagos encontrados
     */
    @GetMapping("/cedula/{cedula}/rango")
    @Operation(
            summary = "Buscar pagos por cédula y rango",
            description = "Consulta pagos por cédula y rango de fechas"
    )
    public ResponseEntity<List<PagoResponseDTO>>
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
                pagoService.buscarPorCedulaYRangoDeFechas(
                        cedula,
                        fechaInicial,
                        fechaFinal
                )
        );
    }

    /**
     * Calcula el total pagado asociado a una cédula.
     *
     * Ruta:
     * GET /api/pagos/cedula/{cedula}/total
     *
     * @param cedula cédula del propietario
     * @return HTTP 200 con el total pagado
     */
    @GetMapping("/cedula/{cedula}/total")
    @Operation(
            summary = "Calcular total pagado por cédula",
            description = "Suma todos los pagos asociados a una cédula"
    )
    public ResponseEntity<BigDecimal> calcularTotalPorCedula(
            @PathVariable String cedula
    ) {
        return ResponseEntity.ok(
                pagoService.calcularTotalPagadoPorCedula(cedula)
        );
    }

    /**
     * Calcula el total pagado para una factura.
     *
     * Ruta:
     * GET /api/pagos/factura/{facturaId}/total
     *
     * @param facturaId identificador de la factura
     * @return HTTP 200 con la suma de sus pagos
     */
    @GetMapping("/factura/{facturaId}/total")
    @Operation(
            summary = "Calcular total pagado por factura",
            description = "Suma todos los pagos registrados para una factura"
    )
    public ResponseEntity<BigDecimal> calcularTotalPorFactura(
            @PathVariable Long facturaId
    ) {
        return ResponseEntity.ok(
                pagoService.calcularTotalPagadoPorFactura(
                        facturaId
                )
        );
    }

    /**
     * Calcula el saldo pendiente de una factura.
     *
     * Saldo pendiente = valor de la factura - total pagado.
     *
     * Ruta:
     * GET /api/pagos/factura/{facturaId}/saldo
     *
     * @param facturaId identificador de la factura
     * @return HTTP 200 con el saldo pendiente
     */
    @GetMapping("/factura/{facturaId}/saldo")
    @Operation(
            summary = "Calcular saldo pendiente",
            description = "Calcula el valor pendiente de pago de una factura"
    )
    public ResponseEntity<BigDecimal> calcularSaldoPendiente(
            @PathVariable Long facturaId
    ) {
        return ResponseEntity.ok(
                pagoService.calcularSaldoPendiente(facturaId)
        );
    }

    /**
     * Calcula el total pagado asociado a un usuario.
     *
     * Ruta:
     * GET /api/pagos/usuario/{usuarioId}/total
     *
     * @param usuarioId identificador del usuario
     * @return HTTP 200 con el total pagado
     */
    @GetMapping("/usuario/{usuarioId}/total")
    @Operation(
            summary = "Calcular total pagado por usuario",
            description = "Suma todos los pagos asociados a un usuario"
    )
    public ResponseEntity<BigDecimal> calcularTotalPorUsuario(
            @PathVariable Long usuarioId
    ) {
        return ResponseEntity.ok(
                pagoService.calcularTotalPagadoPorUsuario(
                        usuarioId
                )
        );
    }

    /**
     * Registra un nuevo pago mediante PagoRequestDTO.
     *
     * Ruta:
     * POST /api/pagos
     *
     * Ejemplo de cuerpo JSON:
     * {
     *   "facturaId": 1,
     *   "valorPagado": 25000.00,
     *   "fechaPago": "2026-11-02",
     *   "metodoPago": "TRANSFERENCIA",
     *   "observacion": "Abono realizado por el propietario"
     * }
     *
     * PagoService valida el saldo pendiente y actualiza
     * automáticamente el estado de la factura.
     *
     * @param dto datos necesarios para registrar el pago
     * @return HTTP 201 con el PagoResponseDTO creado
     */
    @PostMapping
    @Operation(
            summary = "Registrar un pago",
            description = "Registra un pago y actualiza el estado de la factura"
    )
    public ResponseEntity<PagoResponseDTO> registrar(
            @RequestBody PagoRequestDTO dto
    ) {
        PagoResponseDTO pagoRegistrado =
                pagoService.registrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pagoRegistrado);
    }

    /**
     * Elimina un pago y solicita al servicio recalcular
     * el estado de la factura relacionada.
     *
     * Ruta:
     * DELETE /api/pagos/{id}
     *
     * @param id identificador del pago que se eliminará
     * @return HTTP 204 cuando la eliminación sea correcta
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un pago",
            description = "Elimina un pago y actualiza nuevamente el estado de la factura"
    )
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {
        pagoService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
