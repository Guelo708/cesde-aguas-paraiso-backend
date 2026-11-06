package com.cesde.aguas_paraiso.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para registrar o actualizar un tipo de predio")
public record TipoPredioRequestDTO(

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