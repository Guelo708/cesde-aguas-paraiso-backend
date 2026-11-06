package com.cesde.aguas_paraiso.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de un usuario registrado")
public record UsuarioResponseDTO(

        @Schema(description = "Identificador único del usuario", example = "1") Long id,

        @Schema(description = "Número de cédula", example = "1037654321") String cedula,

        @Schema(description = "Nombre completo del usuario", example = "Ana Maria Gomez Lopez") String nombreCompleto,

        @Schema(description = "Primer nombre", example = "Ana") String primerNombre,

        @Schema(description = "Segundo nombre", example = "Maria") String segundoNombre,

        @Schema(description = "Primer apellido", example = "Gomez") String primerApellido,

        @Schema(description = "Segundo apellido", example = "Lopez") String segundoApellido,

        @Schema(description = "Número celular", example = "3001234567") String celular,

        @Schema(description = "Correo electrónico", example = "ana@gmail.com") String correoElectronico

) {
}