package com.cesde.aguas_paraiso.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para registrar o actualizar una tarifa")
public record TarifaRequestDTO(

        @Schema(
                description = "Identificador del tipo de predio",
                example = "1"
        )
        Long tipoPredioId,

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