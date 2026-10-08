package com.cesde.aguas_paraiso.controller;

import com.cesde.aguas_paraiso.model.entity.Factura;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/facturas")
@Tag(
        name = "6. Facturas",
        description = "Generacion, consultas y administracion de facturas"
)
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(
            FacturaService facturaService
    ) {
        this.facturaService = facturaService;
    }

    // Lista todas las facturas registradas.
    @GetMapping
    @Operation(
            summary = "Listar todas las facturas",
            description = "Consulta todas las facturas registradas"
    )
    public ResponseEntity<List<Factura>> listarTodas() {
        return ResponseEntity.ok(facturaService.listarTodas());
    }

    // Busca una factura por su identificador.
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar factura por ID",
            description = "Consulta una factura mediante su identificador"
    )
    public ResponseEntity<Factura> buscarPorId(
            @PathVariable Long id
    ) {
        return facturaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Busca una factura por predio, mes y anio.
    @GetMapping("/predio/{predioId}/periodo")
    @Operation(
            summary = "Buscar factura por predio y periodo",
            description = "Consulta una factura usando el predio, mes y anio"
    )
    public ResponseEntity<Factura> buscarPorPredioYPeriodo(
            @PathVariable Long predioId,
            @RequestParam Integer mes,
            @RequestParam Integer anio
    ) {
        return facturaService
                .buscarPorPredioYPeriodo(predioId, mes, anio)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Lista el historial de facturas de un predio.
    @GetMapping("/predio/{predioId}")
    @Operation(
            summary = "Buscar facturas por predio",
            description = "Consulta el historial de facturas de un predio"
    )
    public ResponseEntity<List<Factura>> buscarPorPredio(
            @PathVariable Long predioId
    ) {
        return ResponseEntity.ok(
                facturaService.buscarPorPredio(predioId)
        );
    }

    // Lista las facturas asociadas a un usuario.
    @GetMapping("/usuario/{usuarioId}")
    @Operation(
            summary = "Buscar facturas por usuario",
            description = "Consulta las facturas asociadas a los predios de un usuario"
    )
    public ResponseEntity<List<Factura>> buscarPorUsuario(
            @PathVariable Long usuarioId
    ) {
        return ResponseEntity.ok(
                facturaService.buscarPorUsuario(usuarioId)
        );
    }

    // Lista las facturas historicas de un propietario por cedula.
    @GetMapping("/cedula/{cedula}")
    @Operation(
            summary = "Buscar facturas por cedula",
            description = "Consulta el historial de facturas usando la cedula del propietario"
    )
    public ResponseEntity<List<Factura>> buscarPorCedula(
            @PathVariable String cedula
    ) {
        return ResponseEntity.ok(
                facturaService.buscarPorCedulaPropietario(cedula)
        );
    }

    // Lista las facturas pendientes, parciales y vencidas por cedula.
    @GetMapping("/cedula/{cedula}/por-pagar")
    @Operation(
            summary = "Buscar facturas por pagar",
            description = "Consulta facturas pendientes, parciales y vencidas por cedula"
    )
    public ResponseEntity<List<Factura>> buscarPorPagarPorCedula(
            @PathVariable String cedula
    ) {
        return ResponseEntity.ok(
                facturaService.buscarFacturasPorPagarPorCedula(cedula)
        );
    }

    // Lista las facturas de un usuario filtradas por estado.
    @GetMapping("/usuario/{usuarioId}/estado/{estado}")
    @Operation(
            summary = "Buscar facturas por usuario y estado",
            description = "Consulta las facturas de un usuario filtradas por estado"
    )
    public ResponseEntity<List<Factura>> buscarPorUsuarioYEstado(
            @PathVariable Long usuarioId,
            @PathVariable EstadoFactura estado
    ) {
        return ResponseEntity.ok(
                facturaService.buscarPorUsuarioYEstado(usuarioId, estado)
        );
    }

    // Lista las facturas de una cedula filtradas por estado.
    @GetMapping("/cedula/{cedula}/estado/{estado}")
    @Operation(
            summary = "Buscar facturas por cedula y estado",
            description = "Consulta las facturas de una cedula filtradas por estado"
    )
    public ResponseEntity<List<Factura>> buscarPorCedulaYEstado(
            @PathVariable String cedula,
            @PathVariable EstadoFactura estado
    ) {
        return ResponseEntity.ok(
                facturaService.buscarPorCedulaYEstado(cedula, estado)
        );
    }

    // Cuenta las facturas de un usuario para un estado.
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
                facturaService.contarPorUsuarioYEstado(usuarioId, estado)
        );
    }

    // Cuenta las facturas de una cedula para un estado.
    @GetMapping("/cedula/{cedula}/estado/{estado}/cantidad")
    @Operation(
            summary = "Contar facturas por cedula y estado",
            description = "Cuenta las facturas de una cedula para un estado determinado"
    )
    public ResponseEntity<Long> contarPorCedulaYEstado(
            @PathVariable String cedula,
            @PathVariable EstadoFactura estado
    ) {
        return ResponseEntity.ok(
                facturaService.contarPorCedulaYEstado(cedula, estado)
        );
    }

    // Busca facturas por cedula dentro de un rango de fechas.
    @GetMapping("/cedula/{cedula}/rango")
    @Operation(
            summary = "Buscar facturas por cedula y rango",
            description = "Consulta facturas por cedula y rango de fechas de generacion"
    )
    public ResponseEntity<List<Factura>> buscarPorCedulaYRango(
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

    // Busca facturas por cedula, estado y rango de fechas.
    @GetMapping("/cedula/{cedula}/estado/{estado}/rango")
    @Operation(
            summary = "Buscar facturas por cedula, estado y rango",
            description = "Consulta facturas aplicando cedula, estado y rango de fechas"
    )
    public ResponseEntity<List<Factura>> buscarPorCedulaEstadoYRango(
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
                facturaService.buscarPorCedulaEstadoYRangoDeFechas(
                        cedula,
                        estado,
                        fechaInicial,
                        fechaFinal
                )
        );
    }

    // Genera una factura para un predio y periodo.
    @PostMapping("/generar")
    @Operation(
            summary = "Generar una factura",
            description = "Genera la factura de un predio para un mes y anio"
    )
    public ResponseEntity<Factura> generarFactura(
            @RequestParam Long predioId,
            @RequestParam Integer mes,
            @RequestParam Integer anio
    ) {
        Factura factura = facturaService.generarFactura(
                predioId,
                mes,
                anio
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(factura);
    }

    // Genera las facturas mensuales de todos los predios pendientes.
    @PostMapping("/generar-mensuales")
    @Operation(
            summary = "Generar facturas mensuales",
            description = "Genera las facturas de todos los predios para un mes y anio"
    )
    public ResponseEntity<List<Factura>> generarFacturasMensuales(
            @RequestParam Integer mes,
            @RequestParam Integer anio
    ) {
        List<Factura> facturas =
                facturaService.generarFacturasMensuales(mes, anio);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(facturas);
    }

    // Actualiza a vencida las facturas que superaron su fecha limite.
    @PutMapping("/actualizar-vencidas")
    @Operation(
            summary = "Actualizar facturas vencidas",
            description = "Marca como vencidas las facturas no pagadas que superaron su fecha limite"
    )
    public ResponseEntity<Integer> actualizarFacturasVencidas() {
        return ResponseEntity.ok(
                facturaService.actualizarFacturasVencidas()
        );
    }

    // Elimina una factura por su identificador.
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
