package com.cesde.aguas_paraiso.controller;

import com.cesde.aguas_paraiso.model.entity.Pago;
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

@RestController
@RequestMapping("/api/pagos")
@Tag(
        name = "7. Pagos",
        description = "Registro, consultas y calculos relacionados con pagos"
)
public class PagoController {

    private final PagoService pagoService;

    public PagoController(
            PagoService pagoService
    ) {
        this.pagoService = pagoService;
    }

    // Lista todos los pagos registrados.
    @GetMapping
    @Operation(
            summary = "Listar todos los pagos",
            description = "Consulta todos los pagos registrados"
    )
    public ResponseEntity<List<Pago>> listarTodos() {
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    // Busca un pago por su identificador.
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar pago por ID",
            description = "Consulta un pago mediante su identificador"
    )
    public ResponseEntity<Pago> buscarPorId(
            @PathVariable Long id
    ) {
        return pagoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Lista los pagos asociados a una factura.
    @GetMapping("/factura/{facturaId}")
    @Operation(
            summary = "Buscar pagos por factura",
            description = "Consulta los pagos asociados a una factura"
    )
    public ResponseEntity<List<Pago>> buscarPorFactura(
            @PathVariable Long facturaId
    ) {
        return ResponseEntity.ok(
                pagoService.buscarPorFactura(facturaId)
        );
    }

    // Lista los pagos asociados a los predios de un usuario.
    @GetMapping("/usuario/{usuarioId}")
    @Operation(
            summary = "Buscar pagos por usuario",
            description = "Consulta los pagos asociados a un usuario"
    )
    public ResponseEntity<List<Pago>> buscarPorUsuario(
            @PathVariable Long usuarioId
    ) {
        return ResponseEntity.ok(
                pagoService.buscarPorUsuario(usuarioId)
        );
    }

    // Lista los pagos asociados a una cedula.
    @GetMapping("/cedula/{cedula}")
    @Operation(
            summary = "Buscar pagos por cedula",
            description = "Consulta los pagos asociados a la cedula de un propietario"
    )
    public ResponseEntity<List<Pago>> buscarPorCedula(
            @PathVariable String cedula
    ) {
        return ResponseEntity.ok(
                pagoService.buscarPorCedula(cedula)
        );
    }

    // Busca pagos por cedula dentro de un rango de fechas.
    @GetMapping("/cedula/{cedula}/rango")
    @Operation(
            summary = "Buscar pagos por cedula y rango",
            description = "Consulta pagos por cedula y rango de fechas"
    )
    public ResponseEntity<List<Pago>> buscarPorCedulaYRango(
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

    // Calcula el total pagado por una cedula.
    @GetMapping("/cedula/{cedula}/total")
    @Operation(
            summary = "Calcular total pagado por cedula",
            description = "Suma todos los pagos asociados a una cedula"
    )
    public ResponseEntity<BigDecimal> calcularTotalPorCedula(
            @PathVariable String cedula
    ) {
        return ResponseEntity.ok(
                pagoService.calcularTotalPagadoPorCedula(cedula)
        );
    }

    // Calcula el total pagado de una factura.
    @GetMapping("/factura/{facturaId}/total")
    @Operation(
            summary = "Calcular total pagado por factura",
            description = "Suma todos los pagos registrados para una factura"
    )
    public ResponseEntity<BigDecimal> calcularTotalPorFactura(
            @PathVariable Long facturaId
    ) {
        return ResponseEntity.ok(
                pagoService.calcularTotalPagadoPorFactura(facturaId)
        );
    }

    // Calcula el saldo pendiente de una factura.
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

    // Calcula el total pagado por un usuario.
    @GetMapping("/usuario/{usuarioId}/total")
    @Operation(
            summary = "Calcular total pagado por usuario",
            description = "Suma todos los pagos asociados a un usuario"
    )
    public ResponseEntity<BigDecimal> calcularTotalPorUsuario(
            @PathVariable Long usuarioId
    ) {
        return ResponseEntity.ok(
                pagoService.calcularTotalPagadoPorUsuario(usuarioId)
        );
    }

    // Registra un nuevo pago y recalcula el estado de la factura.
    @PostMapping
    @Operation(
            summary = "Registrar un pago",
            description = "Registra un pago y actualiza el estado de la factura"
    )
    public ResponseEntity<Pago> registrar(
            @RequestBody Pago pago
    ) {
        Pago pagoRegistrado = pagoService.registrar(pago);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pagoRegistrado);
    }

    // Elimina un pago y recalcula el estado de la factura.
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
