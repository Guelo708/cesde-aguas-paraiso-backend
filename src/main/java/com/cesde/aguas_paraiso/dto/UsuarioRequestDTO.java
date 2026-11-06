package com.cesde.aguas_paraiso.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para registrar o actualizar un usuario")
public record UsuarioRequestDTO(

        @Schema(
                description = "Número de cédula del usuario",
                example = "1037654321"
        )
        String cedula,

        @Schema(
                description = "Primer nombre",
                example = "Ana"
        )
        String primerNombre,

        @Schema(
                description = "Segundo nombre",
                example = "Maria"
        )
        String segundoNombre,

        @Schema(
                description = "Primer apellido",
                example = "Gomez"
        )
        String primerApellido,

        @Schema(
                description = "Segundo apellido",
                example = "Lopez"
        )
        String segundoApellido,

        @Schema(
                description = "Número celular",
                example = "3001234567"
        )
        String celular,

        @Schema(
                description = "Correo electrónico",
                example = "ana@gmail.com"
        )
        String correoElectronico

) {
}