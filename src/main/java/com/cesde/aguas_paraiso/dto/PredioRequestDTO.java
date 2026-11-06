package com.cesde.aguas_paraiso.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para registrar o actualizar un predio")
public record PredioRequestDTO(

        @Schema(
                description = "Código único asignado al predio",
                example = "AP-0001"
        )
        String codigoPredio,

        @Schema(
                description = "Identificador del usuario propietario",
                example = "2"
        )
        Long propietarioId,

        @Schema(
                description = "Identificador del sector al que pertenece el predio",
                example = "1"
        )
        Long sectorId,

        @Schema(
                description = "Identificador del tipo de predio",
                example = "1"
        )
        Long tipoPredioId,

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
                description = "Información adicional sobre el predio o su ubicación",
                example = "Entrada ubicada junto a la vía principal"
        )
        String observaciones

) {
}