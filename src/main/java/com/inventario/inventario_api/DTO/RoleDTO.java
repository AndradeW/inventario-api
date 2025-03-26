package com.inventario.inventario_api.DTO;

import lombok.Builder;

import java.util.List;

@Builder
public record RoleDTO(Long id, String name, String description, List<String> permissions) {
}
