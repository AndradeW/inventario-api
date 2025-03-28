package com.inventario.inventario_api.DTO;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record UserInputDTO(
        @NotNull(message = "El nombre de usuario no puede estar vacío")
        @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El nombre de usuario solo puede contener letras, números, guiones y puntos")
        String username,

        @Email(message = "Debe ser una dirección de correo electrónico válida")
        @NotBlank(message = "El correo electrónico no puede estar vacío")
        String email,

        @NotNull(message = "La contraseña no puede estar vacía")
        @Size(min = 8, max = 30, message = "La contraseña debe tener al menos 8 caracteres")
        //TODO Validar
//    @Pattern(regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}", message = "La contraseña debe contener al menos una letra mayúscula, una letra minúscula, un número y un carácter especial")
        String password,

        String[] role,

        String name,
        String address,
        String phone
) {
}
