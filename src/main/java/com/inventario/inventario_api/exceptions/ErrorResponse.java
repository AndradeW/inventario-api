package com.inventario.inventario_api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

    private HttpStatus statusCode;  // Código de estado HTTP
    private String message;  // Mensaje de error
    private Object details;  // Detalles adicionales (opcional)

}
