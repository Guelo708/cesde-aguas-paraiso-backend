package com.cesde.aguas_paraiso.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.cesde.aguas_paraiso.model.enums.MetodoPago;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos necesarios para registrar un pago")
public record PagoRequestDTO(
        @Schema(description = "Identificador de la factura a la que se aplicará el pago", example = "1") Long facturaId,
        @Schema(description = "Valor abonado a la factura", example = "3000.00") BigDecimal valorPagado,
        @Schema(description = "Fecha en que se realizó el pago", example = "2026-10-16") LocalDate fechaPago,
        @Schema(description = "Método utilizado para realizar el pago", example = "NEQUI", allowableValues = {
                "EFECTIVO", "TRANSFERENCIA", "NEQUI", "TARJETA_DEBITO", "TARJETA_CREDITO", "DAVIPLATA",
                "RECAUDO_CONVENIO" }) MetodoPago metodoPago,
        @Schema(description = "Observación opcional relacionada con el pago", example = "Primer abono a la factura residencial") String observacion) {
}