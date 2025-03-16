package com.inventario.inventario_api.DTO;

import jakarta.validation.constraints.NotBlank;

public record UserLoginDTO(@NotBlank() String username,
                           @NotBlank() String password) {
}
