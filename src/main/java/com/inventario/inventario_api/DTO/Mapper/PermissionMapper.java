package com.inventario.inventario_api.DTO.Mapper;

import com.inventario.inventario_api.DTO.PermissionDTO;
import com.inventario.inventario_api.model.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permission permissionDTOToPermission(PermissionDTO permissionDTO);

    default String toUpperCase(String name) {
        return name != null ? name.toUpperCase() : null;
    }
}
