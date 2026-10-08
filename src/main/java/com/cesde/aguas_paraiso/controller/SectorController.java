package com.cesde.aguas_paraiso.controller;

import com.cesde.aguas_paraiso.model.entity.Sector;
import com.cesde.aguas_paraiso.service.SectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de recibir las solicitudes HTTP
 * relacionadas con los sectores del Acueducto Aguas Paraíso.
 *
 * Un sector representa una división geográfica u operativa
 * utilizada para organizar los predios del acueducto.
 *
 * Este controlador permite:
 *
 * - Listar todos los sectores.
 * - Buscar un sector por ID.
 * - Buscar un sector por nombre exacto.
 * - Buscar sectores mediante una parte del nombre.
 * - Registrar sectores.
 * - Actualizar sectores.
 * - Eliminar sectores.
 *
 * Las reglas del negocio se encuentran en SectorService.
 */
@RestController
@RequestMapping("/api/sectores")
@Tag(
        name = "2. Sectores",
        description = "Operaciones CRUD y consultas de sectores"
)
public class SectorController {

    /**
     * Servicio que contiene la lógica del negocio
     * relacionada con los sectores.
     *
     * La dependencia se declara final porque se asigna
     * una sola vez mediante el constructor.
     */
    private final SectorService sectorService;

    /**
     * Constructor utilizado por Spring para inyectar
     * automáticamente una instancia de SectorService.
     *
     * @param sectorService servicio encargado de administrar
     *                      la lógica de los sectores
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
     * @return HTTP 200 con la lista de sectores
     */
    @GetMapping
    @Operation(
            summary = "Listar todos los sectores",
            description = "Consulta todos los sectores registrados en la base de datos"
    )
    public ResponseEntity<List<Sector>> listarTodos() {

        List<Sector> sectores =
                sectorService.listarTodos();

        return ResponseEntity.ok(sectores);
    }

    /**
     * Busca un sector utilizando su identificador interno.
     *
     * Ejemplo:
     * GET /api/sectores/1
     *
     * @param id identificador interno del sector
     * @return HTTP 200 si el sector existe
     *         o HTTP 404 si no fue encontrado
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar sector por ID",
            description = "Consulta un sector mediante el identificador heredado de BaseEntity"
    )
    public ResponseEntity<Sector> buscarPorId(
            @PathVariable Long id
    ) {

        return sectorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    /**
     * Busca un sector utilizando el nombre completo.
     *
     * La búsqueda ignora diferencias entre mayúsculas
     * y minúsculas.
     *
     * Por ejemplo, los nombres "Centro", "CENTRO"
     * y "centro" se consideran equivalentes.
     *
     * Ejemplo:
     * GET /api/sectores/nombre/Centro
     *
     * @param nombre nombre exacto del sector
     * @return HTTP 200 si el sector existe
     *         o HTTP 404 si no fue encontrado
     */
    @GetMapping("/nombre/{nombre}")
    @Operation(
            summary = "Buscar sector por nombre",
            description = "Consulta un sector por su nombre exacto ignorando mayúsculas y minúsculas"
    )
    public ResponseEntity<Sector> buscarPorNombre(
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
     * La búsqueda es parcial e ignora diferencias entre
     * mayúsculas y minúsculas.
     *
     * Por ejemplo, el texto "cen" podría encontrar:
     *
     * - Centro.
     * - Centro Norte.
     * - Sector Central.
     *
     * Ruta:
     * GET /api/sectores/buscar?nombre=cen
     *
     * @param nombre texto que debe formar parte del nombre
     * @return HTTP 200 con los sectores encontrados
     */
    @GetMapping("/buscar")
    @Operation(
            summary = "Buscar sectores por nombre parcial",
            description = "Busca sectores cuyo nombre contenga el texto enviado"
    )
    public ResponseEntity<List<Sector>> buscarPorNombreParcial(
            @RequestParam String nombre
    ) {

        List<Sector> sectores =
                sectorService.buscarPorNombreParcial(nombre);

        return ResponseEntity.ok(sectores);
    }

    /**
     * Registra un nuevo sector.
     *
     * Ruta:
     * POST /api/sectores
     *
     * La anotación @RequestBody convierte el JSON recibido
     * en un objeto de tipo Sector.
     *
     * SectorService valida que no exista otro sector
     * registrado con el mismo nombre.
     *
     * @param sector datos del sector que será registrado
     * @return HTTP 201 con el sector registrado
     */
    @PostMapping
    @Operation(
            summary = "Registrar un sector",
            description = "Crea un sector validando que el nombre no esté registrado"
    )
    public ResponseEntity<Sector> registrar(
            @RequestBody Sector sector
    ) {

        Sector sectorRegistrado =
                sectorService.registrar(sector);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sectorRegistrado);
    }

    /**
     * Actualiza la información de un sector existente.
     *
     * Ruta:
     * PUT /api/sectores/{id}
     *
     * El ID se recibe mediante la URL y los nuevos datos
     * se reciben dentro del cuerpo JSON.
     *
     * SectorService verifica:
     *
     * - Que el sector exista.
     * - Que el nombre no pertenezca a otro sector.
     *
     * @param id identificador del sector que será actualizado
     * @param sector nuevos datos del sector
     * @return HTTP 200 con el sector actualizado
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un sector",
            description = "Actualiza el nombre y la descripción de un sector existente"
    )
    public ResponseEntity<Sector> actualizar(
            @PathVariable Long id,
            @RequestBody Sector sector
    ) {

        Sector sectorActualizado =
                sectorService.actualizar(
                        id,
                        sector
                );

        return ResponseEntity.ok(
                sectorActualizado
        );
    }

    /**
     * Elimina un sector mediante su identificador interno.
     *
     * Ruta:
     * DELETE /api/sectores/{id}
     *
     * SectorService verifica primero que el sector exista.
     *
     * @param id identificador del sector que será eliminado
     * @return HTTP 204 cuando la eliminación sea exitosa
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