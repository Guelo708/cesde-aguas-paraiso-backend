package com.cesde.aguas_paraiso.controller;

import com.cesde.aguas_paraiso.model.entity.Predio;
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

@RestController
@RequestMapping("/api/predios")
@Tag(name = "4. Predios", description = "Operaciones CRUD y consultas de predios")
public class PredioController {

    private final PredioService predioService;

    public PredioController(
            PredioService predioService) {
        this.predioService = predioService;
    }

    // Lista todos los predios registrados.
    @GetMapping
    @Operation(summary = "Listar todos los predios", description = "Consulta todos los predios registrados")
    public ResponseEntity<List<Predio>> listarTodos() {

        List<Predio> predios = predioService.listarTodos();

        return ResponseEntity.ok(predios);
    }

    // Busca un predio por su identificador interno.
    @GetMapping("/{id}")
    @Operation(summary = "Buscar predio por ID", description = "Consulta un predio mediante su identificador")
    public ResponseEntity<Predio> buscarPorId(
            @PathVariable Long id) {

        return predioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Busca un predio por su codigo unico.
    @GetMapping("/codigo/{codigoPredio}")
    @Operation(summary = "Buscar predio por codigo", description = "Consulta un predio mediante su codigo unico")
    public ResponseEntity<Predio> buscarPorCodigo(
            @PathVariable String codigoPredio) {

        return predioService.buscarPorCodigo(codigoPredio)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Lista los predios asociados a un propietario.
    @GetMapping("/propietario/{usuarioId}")
    @Operation(summary = "Buscar predios por propietario", description = "Consulta los predios asociados al ID de un usuario propietario")
    public ResponseEntity<List<Predio>> buscarPorPropietario(
            @PathVariable Long usuarioId) {

        List<Predio> predios = predioService.buscarPorPropietario(usuarioId);

        return ResponseEntity.ok(predios);
    }

    // Lista los predios asociados a un sector.
    @GetMapping("/sector/{sectorId}")
    @Operation(summary = "Buscar predios por sector", description = "Consulta los predios asociados al ID de un sector")
    public ResponseEntity<List<Predio>> buscarPorSector(
            @PathVariable Long sectorId) {

        List<Predio> predios = predioService.buscarPorSector(sectorId);

        return ResponseEntity.ok(predios);
    }

    // Lista los predios asociados a un tipo de predio.
    @GetMapping("/tipo-predio/{tipoPredioId}")
    @Operation(summary = "Buscar predios por tipo de predio", description = "Consulta los predios asociados al ID de un tipo de predio")
    public ResponseEntity<List<Predio>> buscarPorTipoPredio(
            @PathVariable Long tipoPredioId) {

        List<Predio> predios = predioService.buscarPorTipoPredio(tipoPredioId);

        return ResponseEntity.ok(predios);
    }

    // Registra un nuevo predio.
    // Las relaciones se reciben como objetos que contienen su ID.
    @PostMapping
    @Operation(summary = "Registrar un predio", description = "Crea un predio y valida su codigo, propietario, sector y tipo de predio")
    public ResponseEntity<Predio> registrar(
            @RequestBody Predio predio) {

        Predio predioRegistrado = predioService.registrar(predio);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(predioRegistrado);
    }

    // Actualiza un predio existente.
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un predio", description = "Actualiza el codigo, la ubicacion y las relaciones de un predio")
    public ResponseEntity<Predio> actualizar(
            @PathVariable Long id,
            @RequestBody Predio predio) {

        Predio predioActualizado = predioService.actualizar(
                id,
                predio);

        return ResponseEntity.ok(predioActualizado);
    }

    // Elimina un predio por su identificador.
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un predio", description = "Elimina un predio mediante su identificador")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        predioService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}