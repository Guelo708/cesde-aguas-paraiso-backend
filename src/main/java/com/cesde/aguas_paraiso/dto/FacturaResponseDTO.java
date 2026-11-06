package com.cesde.aguas_paraiso.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información completa de una factura generada")
public record FacturaResponseDTO(

        @Schema(
                description = "Identificador único de la factura",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Identificador interno del predio facturado",
                example = "1"
        )
        Long predioId,

        @Schema(
                description = "Código del predio facturado",
                example = "AP-0001"
        )
        String codigoPredio,

        @Schema(
                description = "Identificador de la tarifa aplicada",
                example = "1"
        )
        Long tarifaId,

        @Schema(
                description = "Nombre del tipo de predio asociado a la tarifa",
                example = "Residencial"
        )
        String tipoPredio,

        @Schema(
                description = "Número de cédula del propietario al generar la factura",
                example = "1037654321"
        )
        String cedulaPropietario,

        @Schema(
                description = "Nombre completo del propietario al generar la factura",
                example = "Ana María Gómez López"
        )
        String nombrePropietario,

        @Schema(
                description = "Mes correspondiente al periodo facturado",
                example = "10"
        )
        Integer mes,

        @Schema(
                description = "Año correspondiente al periodo facturado",
                example = "2026"
        )
        Integer anio,

        @Schema(
                description = "Valor total de la factura",
                example = "8000.00"
        )
        BigDecimal valorFactura,

        @Schema(
                description = "Fecha de generación de la factura",
                example = "2026-10-01"
        )
        LocalDate fechaGeneracion,

        @Schema(
                description = "Fecha límite de pago",
                example = "2026-10-15"
        )
        LocalDate fechaVencimiento,

        @Schema(
                description = "Estado actual de la factura",
                example = "PENDIENTE",
                allowableValues = {
                        "PENDIENTE",
                        "PARCIAL",
                        "PAGADA",
                        "VENCIDA"
                }
        )
        String estado,

        @Schema(
                description = "Fecha y hora en que se creó el registro",
                example = "2026-10-01T08:30:00"
        )
        LocalDateTime fechaCreacion,

        @Schema(
                description = "Fecha y hora de la última actualización",
                example = "2026-10-16T14:45:00"
        )
        LocalDateTime fechaActualizacion

) {
}
