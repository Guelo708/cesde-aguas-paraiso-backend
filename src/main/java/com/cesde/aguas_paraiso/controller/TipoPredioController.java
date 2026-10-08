package com.cesde.aguas_paraiso.controller;

import com.cesde.aguas_paraiso.model.entity.TipoPredio;
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

@RestController
@RequestMapping("/api/tipos-predio")
@Tag(name = "3. Tipos de predio", description = "Operaciones CRUD y consultas de tipos de predio")
public class TipoPredioController {

    private final TipoPredioService tipoPredioService;

    public TipoPredioController(
            TipoPredioService tipoPredioService) {
        this.tipoPredioService = tipoPredioService;
    }

    // Lista todos los tipos de predio registrados.
    @GetMapping
    @Operation(summary = "Listar todos los tipos de predio", description = "Consulta todos los tipos de predio registrados")
    public ResponseEntity<List<TipoPredio>> listarTodos() {

        List<TipoPredio> tiposPredio = tipoPredioService.listarTodos();

        return ResponseEntity.ok(tiposPredio);
    }

    // Busca un tipo de predio por su identificador.
    @GetMapping("/{id}")
    @Operation(summary = "Buscar tipo de predio por ID", description = "Consulta un tipo de predio mediante su identificador")
    public ResponseEntity<TipoPredio> buscarPorId(
            @PathVariable Long id) {

        return tipoPredioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Busca un tipo de predio por su nombre exacto.
    @GetMapping("/nombre/{nombre}")
    @Operation(summary = "Buscar tipo de predio por nombre", description = "Consulta por nombre exacto ignorando mayusculas y minusculas")
    public ResponseEntity<TipoPredio> buscarPorNombre(
            @PathVariable String nombre) {

        return tipoPredioService.buscarPorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Busca tipos de predio mediante una parte del nombre.
    @GetMapping("/buscar")
    @Operation(summary = "Buscar tipos de predio por nombre parcial", description = "Busca tipos de predio cuyo nombre contenga el texto recibido")
    public ResponseEntity<List<TipoPredio>> buscarPorNombreParcial(
            @RequestParam String nombre) {

        List<TipoPredio> tiposPredio = tipoPredioService.buscarPorNombreParcial(nombre);

        return ResponseEntity.ok(tiposPredio);
    }

    // Registra un nuevo tipo de predio.
    @PostMapping
    @Operation(summary = "Registrar un tipo de predio", description = "Crea un tipo de predio validando que el nombre no este registrado")
    public ResponseEntity<TipoPredio> registrar(
            @RequestBody TipoPredio tipoPredio) {

        TipoPredio tipoPredioRegistrado = tipoPredioService.registrar(tipoPredio);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tipoPredioRegistrado);
    }

    // Actualiza un tipo de predio existente.
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un tipo de predio", description = "Actualiza el nombre y la descripcion de un tipo de predio")
    public ResponseEntity<TipoPredio> actualizar(
            @PathVariable Long id,
            @RequestBody TipoPredio tipoPredio) {

        TipoPredio tipoPredioActualizado = tipoPredioService.actualizar(
                id,
                tipoPredio);

        return ResponseEntity.ok(tipoPredioActualizado);
    }

    // Elimina un tipo de predio por su identificador.
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un tipo de predio", description = "Elimina un tipo de predio mediante su identificador")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        tipoPredioService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
