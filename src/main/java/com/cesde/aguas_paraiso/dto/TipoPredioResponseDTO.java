package com.cesde.aguas_paraiso.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de un tipo de predio registrado")
public record TipoPredioResponseDTO(

        @Schema(
                description = "Identificador único del tipo de predio",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Nombre del tipo de predio",
                example = "Residencial"
        )
        String nombre,

        @Schema(
                description = "Descripción del tipo de predio",
                example = "Vivienda destinada para uso habitacional"
        )
        String descripcion

) {
}