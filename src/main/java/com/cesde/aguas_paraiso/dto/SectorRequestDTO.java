package com.cesde.aguas_paraiso.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para registrar o actualizar un sector")
public record SectorRequestDTO(

        @Schema(
                description = "Nombre del sector",
                example = "Tablazo Centro"
        )
        String nombre,

        @Schema(
                description = "Descripción del sector",
                example = "Sector central de distribución del servicio"
        )
        String descripcion

) {
}
