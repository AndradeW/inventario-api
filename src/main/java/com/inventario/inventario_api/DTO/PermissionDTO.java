package com.inventario.inventario_api.DTO;

import jakarta.validation.constraints.NotNull;

public record PermissionDTO(@NotNull String name) {
}
