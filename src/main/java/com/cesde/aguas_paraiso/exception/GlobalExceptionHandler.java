package com.cesde.aguas_paraiso.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador global de excepciones de la API.
 *
 * Esta clase captura las excepciones lanzadas desde
 * los controladores y servicios del proyecto.
 *
 * Su objetivo es devolver respuestas JSON claras,
 * evitando mostrar información técnica como el
 * stack trace al cliente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura las excepciones de tipo IllegalArgumentException.
     *
     * Los servicios utilizan esta excepción cuando se incumple
     * una regla del negocio, por ejemplo:
     *
     * - Cédula duplicada.
     * - Correo electrónico duplicado.
     * - Usuario inexistente.
     * - Valor de pago inválido.
     * - Fecha incorrecta.
     *
     * @param exception excepción generada por una regla del negocio
     * @return respuesta HTTP 400 con información clara del error
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarIllegalArgumentException(
            IllegalArgumentException exception) {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put(
                "fecha",
                LocalDateTime.now());

        respuesta.put(
                "estado",
                HttpStatus.BAD_REQUEST.value());

        respuesta.put(
                "error",
                HttpStatus.BAD_REQUEST.getReasonPhrase());

        respuesta.put(
                "mensaje",
                exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(respuesta);
    }

    /**
     * Captura cualquier excepción que no tenga
     * un manejo específico.
     *
     * Esta respuesta evita mostrar detalles internos
     * de la aplicación al cliente.
     *
     * El stack trace seguirá apareciendo en la terminal
     * del servidor para ayudar durante el desarrollo.
     *
     * @param exception error inesperado de la aplicación
     * @return respuesta HTTP 500 con un mensaje general
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarExcepcionGeneral(
            Exception exception) {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put(
                "fecha",
                LocalDateTime.now());

        respuesta.put(
                "estado",
                HttpStatus.INTERNAL_SERVER_ERROR.value());

        respuesta.put(
                "error",
                HttpStatus.INTERNAL_SERVER_ERROR
                        .getReasonPhrase());

        respuesta.put(
                "mensaje",
                "Ocurrió un error inesperado en el servidor");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(respuesta);
    }
}
