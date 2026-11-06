package com.cesde.aguas_paraiso.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de un sector registrado")
public record SectorResponseDTO(

        @Schema(
                description = "Identificador único del sector",
                example = "1"
        )
        Long id,

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