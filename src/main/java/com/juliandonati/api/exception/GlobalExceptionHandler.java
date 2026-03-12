package com.juliandonati.api.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(),error.getDefaultMessage());
        });

        Map<String, Object> body = new HashMap<>();

        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error","Bad Request");
        body.put("message","Errores de validación");
        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex){
        Map<String, Object> body = new HashMap<>();

        body.put("status",HttpStatus.NOT_FOUND.value());
        body.put("error","Resource Not Found");
        body.put("message",ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex){
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("error","Conflicto de Datos");
        errorDetails.put("message","La operación no se pudo completar debido a un conflicto de datos. Asegurate de que los valores ingresados" +
                "sean únicos y las referencias existan.");
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex){
        System.err.println("Ocurrio un error inesperado: " + ex.getMessage());
        ex.printStackTrace();

        Map<String,String> errorDetails = new HashMap<>();
        errorDetails.put("error","Error interno del servidor."); // No se expone en producción. Solo aparece en la consola del servidor.
        errorDetails.put("message","Ocurrió un error inesperado. Por favor, inténtalo de nuevo más tarde.");

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
