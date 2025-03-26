package com.inventario.inventario_api.DTO.Mapper;

import com.inventario.inventario_api.DTO.PermissionDTO;
import com.inventario.inventario_api.model.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    @Mapping(target = "id", ignore = true)
    Permission toPermission(PermissionDTO permissionDTO);
    PermissionDTO toPermissionDTO(Permission permission);

    @Mapping(target = "id", ignore = true)
    Set<Permission> toPermissionList(List<PermissionDTO> permissionDTOList);
    Set<PermissionDTO> tooPermissionDTOList(List<Permission> permissionList);

    default String toUpperCase(String name) {
        return name != null ? name.toUpperCase() : null;
    }
}
