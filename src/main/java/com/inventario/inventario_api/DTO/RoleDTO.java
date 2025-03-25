package com.inventario.inventario_api.DTO;

import lombok.Builder;

import java.util.Set;

@Builder
public record RoleDTO(String name, String description, Set<PermissionDTO> permissions) {
}
