package com.inventario.inventario_api.DTO.Mapper;

import com.inventario.inventario_api.DTO.PermissionDTO;
import com.inventario.inventario_api.model.Permission;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permission toPermission(PermissionDTO permissionDTO);

    Set<Permission> toPermissionList(Set<PermissionDTO> permissionDTOList);
    Set<PermissionDTO> permissionListToPermissionDTOList(Set<Permission> permissionList);

    default String toUpperCase(String name) {
        return name != null ? name.toUpperCase() : null;
    }
}
