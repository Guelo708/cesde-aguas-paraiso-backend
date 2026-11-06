package com.cesde.aguas_paraiso.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de una tarifa registrada")
public record TarifaResponseDTO(

        @Schema(
                description = "Identificador único de la tarifa",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Tipo de predio asociado a la tarifa"
        )
        TipoPredioResponseDTO tipoPredio,

        @Schema(
                description = "Valor de la tarifa",
                example = "8000.00"
        )
        BigDecimal valor,

        @Schema(
                description = "Fecha de inicio de vigencia",
                example = "2026-01-01"
        )
        LocalDate fechaInicio,

        @Schema(
                description = "Fecha final de vigencia",
                example = "2026-12-31"
        )
        LocalDate fechaFin

) {
}