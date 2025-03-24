package com.inventario.inventario_api.DTO;

import lombok.Builder;

@Builder
public record RoleDTO(String name, String description, String[] permissions) {
}
