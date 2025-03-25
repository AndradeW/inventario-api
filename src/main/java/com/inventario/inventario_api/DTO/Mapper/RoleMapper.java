package com.inventario.inventario_api.DTO.Mapper;

import com.inventario.inventario_api.DTO.PermissionDTO;
import com.inventario.inventario_api.DTO.RoleDTO;
import com.inventario.inventario_api.model.Permission;
import com.inventario.inventario_api.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", expression = "java(mapPermissionDTOSetToPermissionSet(roleDTO.permissions()))")
    Role toRole(RoleDTO roleDTO);

    default Set<Permission> mapPermissionDTOSetToPermissionSet(Set<PermissionDTO> permissionList) {
        Set<Permission> permissionSet = new HashSet<>();

        if (permissionList == null || permissionList.size() == 0) {
            return permissionSet;
        }

        for (PermissionDTO permission : permissionList) {
            Permission permissions = new Permission();
            permissions.setName(permission.name().toUpperCase());
            permissionSet.add(permissions);
        }

        return permissionSet;
    }

    @Mapping(target = "permissions", expression = "java(mapPermissionToPermissionDTOSet(createdRole.getPermissions()))")
    RoleDTO toRoleDTO(Role createdRole);
    List<RoleDTO> toRoleDTOSet(List<Role> createdRoleSet);


    default Set<PermissionDTO> mapPermissionToPermissionDTOSet(Set<Permission> permissionSet) {

        Set<PermissionDTO> permissionDTOSet = new HashSet<>();

        if (permissionSet == null || permissionSet.size() == 0) {
            return permissionDTOSet;
        }

        for (Permission permission : permissionSet) {
            PermissionDTO permissionDTO = PermissionDTO.builder().name(permission.getName()).build();
            permissionDTOSet.add(permissionDTO);
        }

        return permissionDTOSet;
    }


}
