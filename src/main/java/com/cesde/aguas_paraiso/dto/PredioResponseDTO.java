package com.cesde.aguas_paraiso.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información completa de un predio registrado")
public record PredioResponseDTO(

        @Schema(
                description = "Identificador único del predio",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Código único del predio",
                example = "AP-0001"
        )
        String codigoPredio,

        @Schema(
                description = "Información resumida del propietario"
        )
        UsuarioResumenDTO propietario,

        @Schema(
                description = "Sector al que pertenece el predio"
        )
        SectorResponseDTO sector,

        @Schema(
                description = "Tipo de predio asignado"
        )
        TipoPredioResponseDTO tipoPredio,

        @Schema(
                description = "Nombre con el que se identifica el predio",
                example = "Casa principal"
        )
        String nombrePredio,

        @Schema(
                description = "Vereda donde está ubicado el predio",
                example = "El Tablazo"
        )
        String vereda,

        @Schema(
                description = "Corregimiento donde está ubicado el predio",
                example = "Santa Elena"
        )
        String corregimiento,

        @Schema(
                description = "Municipio donde está ubicado el predio",
                example = "Medellín"
        )
        String municipio,

        @Schema(
                description = "Información adicional sobre el predio",
                example = "Entrada ubicada junto a la vía principal"
        )
        String observaciones,

        @Schema(
                description = "Fecha y hora en que se registró el predio",
                example = "2026-10-15T10:30:00"
        )
        LocalDateTime fechaCreacion,

        @Schema(
                description = "Fecha y hora de la última actualización",
                example = "2026-10-20T14:45:00"
        )
        LocalDateTime fechaActualizacion

) {
}