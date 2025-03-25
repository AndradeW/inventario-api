package com.inventario.inventario_api.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PermissionDTO(@NotNull String name) {
}
