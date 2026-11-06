package com.cesde.aguas_paraiso.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.cesde.aguas_paraiso.model.enums.MetodoPago;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de un pago registrado")
public record PagoResponseDTO(@Schema(description = "Identificador único del pago", example = "1") Long id,
        @Schema(description = "Identificador de la factura pagada", example = "1") Long facturaId,
        @Schema(description = "Código del predio asociado a la factura", example = "AP-0001") String codigoPredio,
        @Schema(description = "Número de cédula del propietario de la factura", example = "1037654321") String cedulaPropietario,
        @Schema(description = "Nombre del propietario de la factura", example = "Ana María Gómez López") String nombrePropietario,
        @Schema(description = "Mes correspondiente a la factura pagada", example = "10") Integer mesFactura,
        @Schema(description = "Año correspondiente a la factura pagada", example = "2026") Integer anioFactura,
        @Schema(description = "Valor original de la factura", example = "8000.00") BigDecimal valorFactura,
        @Schema(description = "Valor abonado mediante este pago", example = "3000.00") BigDecimal valorPagado,
        @Schema(description = "Fecha en que se realizó el pago", example = "2026-10-16") LocalDate fechaPago,
        @Schema(description = "Método utilizado para realizar el pago", example = "NEQUI") MetodoPago metodoPago,
        @Schema(description = "Observación relacionada con el pago", example = "Primer abono a la factura residencial") String observacion,
        @Schema(description = "Estado de la factura después del registro del pago", example = "PARCIAL", allowableValues = {
                "PENDIENTE", "PARCIAL", "PAGADA", "VENCIDA" }) String estadoFactura,
        @Schema(description = "Fecha y hora en que se creó el registro del pago", example = "2026-10-16T09:30:00") LocalDateTime fechaCreacion,
        @Schema(description = "Fecha y hora de la última actualización del pago", example = "2026-10-16T09:30:00") LocalDateTime fechaActualizacion) {
}