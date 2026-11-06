package com.cesde.aguas_paraiso.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información resumida de un usuario")
public record UsuarioResumenDTO(

        @Schema(
                description = "Identificador interno del usuario",
                example = "2"
        )
        Long id,

        @Schema(
                description = "Número de cédula del usuario",
                example = "1037654321"
        )
        String cedula,

        @Schema(
                description = "Nombre completo del usuario",
                example = "Ana María Gómez López"
        )
        String nombreCompleto

) {
}