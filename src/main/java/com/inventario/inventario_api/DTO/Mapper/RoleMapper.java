package com.inventario.inventario_api.DTO.Mapper;

import com.inventario.inventario_api.DTO.RoleDTO;
import com.inventario.inventario_api.model.Permission;
import com.inventario.inventario_api.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", qualifiedByName = "toUpperCaseName")
    @Mapping(target = "permissions", expression = "java(mapPermissionDTOSetToPermissionSet(roleDTO.permissions()))")
    Role toRole(RoleDTO roleDTO);

    @Named("toUpperCaseName")
    default String toUpperCaseName(String name) {
        return name.toUpperCase();
    }

    default Set<Permission> mapPermissionDTOSetToPermissionSet(List<String> permissionList) {
        Set<Permission> permissionSet = new HashSet<>();

        if (permissionList == null || permissionList.size() == 0) {
            return permissionSet;
        }

        for (String permission : permissionList) {
            Permission permissions = new Permission();
            permissions.setName(permission.toUpperCase());
            permissionSet.add(permissions);
        }

        return permissionSet;
    }

    @Mapping(target = "permissions", expression = "java(mapPermissionToPermissionDTOSet(createdRole.getPermissions()))")
    RoleDTO toRoleDTO(Role createdRole);

    List<RoleDTO> toRoleDTOSet(List<Role> createdRoleSet);


    default List<String> mapPermissionToPermissionDTOSet(Set<Permission> permissionSet) {

        List<String> permissionDTOSet = new ArrayList<>();

        if (permissionSet == null || permissionSet.size() == 0) {
            return permissionDTOSet;
        }

        for (Permission permission : permissionSet) {
            String permissionDTO = permission.getName();
            permissionDTOSet.add(permissionDTO);
        }

        return permissionDTOSet;
    }


}
