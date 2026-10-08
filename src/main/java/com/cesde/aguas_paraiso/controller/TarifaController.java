package com.cesde.aguas_paraiso.controller;

import com.cesde.aguas_paraiso.model.entity.Tarifa;
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

@RestController
@RequestMapping("/api/tarifas")
@Tag(name = "5. Tarifas", description = "Operaciones CRUD y consultas del historial de tarifas")
public class TarifaController {

        private final TarifaService tarifaService;

        public TarifaController(
                        TarifaService tarifaService) {
                this.tarifaService = tarifaService;
        }

        // Lista todas las tarifas registradas.
        @GetMapping
        @Operation(summary = "Listar todas las tarifas", description = "Consulta todas las tarifas registradas")
        public ResponseEntity<List<Tarifa>> listarTodas() {

                List<Tarifa> tarifas = tarifaService.listarTodas();

                return ResponseEntity.ok(tarifas);
        }

        // Busca una tarifa por su identificador.
        @GetMapping("/{id}")
        @Operation(summary = "Buscar tarifa por ID", description = "Consulta una tarifa mediante su identificador")
        public ResponseEntity<Tarifa> buscarPorId(
                        @PathVariable Long id) {

                return tarifaService.buscarPorId(id)
                                .map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.notFound().build());
        }

        // Consulta el historial de tarifas de un tipo de predio.
        // El historial se obtiene ordenado desde la tarifa mas reciente.
        @GetMapping("/tipo-predio/{tipoPredioId}/historial")
        @Operation(summary = "Consultar historial de tarifas", description = "Consulta el historial de tarifas de un tipo de predio")
        public ResponseEntity<List<Tarifa>> buscarHistorialPorTipoPredio(
                        @PathVariable Long tipoPredioId) {

                List<Tarifa> tarifas = tarifaService.buscarHistorialPorTipoPredio(
                                tipoPredioId);

                return ResponseEntity.ok(tarifas);
        }

        // Busca la tarifa que estaba vigente para una fecha determinada.
        // La fecha debe enviarse con el formato AAAA-MM-DD.
        @GetMapping("/tipo-predio/{tipoPredioId}/vigente")
        @Operation(summary = "Buscar tarifa vigente", description = "Consulta la tarifa vigente de un tipo de predio para una fecha")
        public ResponseEntity<Tarifa> buscarTarifaVigente(
                        @PathVariable Long tipoPredioId,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

                return tarifaService
                                .buscarTarifaVigente(
                                                tipoPredioId,
                                                fecha)
                                .map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.notFound().build());
        }

        // Registra una nueva tarifa.
        // El tipo de predio se recibe como un objeto que contiene su ID.
        @PostMapping
        @Operation(summary = "Registrar una tarifa", description = "Crea una tarifa y actualiza la vigencia de la tarifa anterior")
        public ResponseEntity<Tarifa> registrar(
                        @RequestBody Tarifa tarifa) {

                Tarifa tarifaRegistrada = tarifaService.registrar(tarifa);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(tarifaRegistrada);
        }

        // Actualiza una tarifa existente.
        @PutMapping("/{id}")
        @Operation(summary = "Actualizar una tarifa", description = "Actualiza el tipo de predio, valor y fechas de una tarifa")
        public ResponseEntity<Tarifa> actualizar(
                        @PathVariable Long id,
                        @RequestBody Tarifa tarifa) {

                Tarifa tarifaActualizada = tarifaService.actualizar(
                                id,
                                tarifa);

                return ResponseEntity.ok(tarifaActualizada);
        }

        // Elimina una tarifa por su identificador.
        @DeleteMapping("/{id}")
        @Operation(summary = "Eliminar una tarifa", description = "Elimina una tarifa mediante su identificador")
        public ResponseEntity<Void> eliminar(
                        @PathVariable Long id) {

                tarifaService.eliminar(id);

                return ResponseEntity
                                .noContent()
                                .build();
        }
}
