package com.inventario.inventario_api.DTO.Mapper;

import com.inventario.inventario_api.DTO.RoleDTO;
import com.inventario.inventario_api.model.Permission;
import com.inventario.inventario_api.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", expression = "java(mapPermissionStringToSet(roleDTO.permissions()))")
    Role roleDTOToRole(RoleDTO roleDTO);

    default Set<Permission> mapPermissionStringToSet(String[] permissionStringList) {
        Set<Permission> permissionSet = new HashSet<>();

        if (permissionStringList == null || permissionStringList.length == 0) {
            return permissionSet;
        }

        for (String permission : permissionStringList) {
            Permission permissions = new Permission();
            permissions.setName(permission.toUpperCase());
            permissionSet.add(permissions);
        }

        return permissionSet;
    }

}
