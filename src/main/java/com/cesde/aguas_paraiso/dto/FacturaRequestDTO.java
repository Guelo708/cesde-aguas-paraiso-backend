package com.cesde.aguas_paraiso.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos necesarios para generar una factura")
public record FacturaRequestDTO(

        @Schema(
                description = "Identificador del predio al que se generará la factura",
                example = "1"
        )
        Long predioId,

        @Schema(
                description = "Mes correspondiente al periodo facturado",
                example = "10",
                minimum = "1",
                maximum = "12"
        )
        Integer mes,

        @Schema(
                description = "Año correspondiente al periodo facturado",
                example = "2026"
        )
        Integer anio

) {
}
