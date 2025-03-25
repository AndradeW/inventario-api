package com.inventario.inventario_api.controller;

import com.inventario.inventario_api.DTO.Mapper.PermissionMapper;
import com.inventario.inventario_api.DTO.Mapper.RoleMapper;
import com.inventario.inventario_api.DTO.PermissionDTO;
import com.inventario.inventario_api.DTO.RoleDTO;
import com.inventario.inventario_api.model.Permission;
import com.inventario.inventario_api.model.Role;
import com.inventario.inventario_api.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    @Autowired
    public RoleController(RoleService roleService, RoleMapper roleMapper, PermissionMapper permissionMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
    }

    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO roleDTO) {
        Role role = this.roleMapper.toRole(roleDTO);
        Role createdRole = this.roleService.createRole(role);
        RoleDTO createdRoleDTO = this.roleMapper.toRoleDTO(createdRole);
        return new ResponseEntity<>(createdRoleDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<Role> roles = this.roleService.getAllRoles();
        return ResponseEntity.ok(this.roleMapper.toRoleDTOSet(roles));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {

        Optional<Role> role = this.roleService.getRoleById(id);

        return role.map(r -> new ResponseEntity<>(this.roleMapper.toRoleDTO(r), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<RoleDTO> getRoleByName(@PathVariable String name) {

        Optional<Role> role = this.roleService.getRoleByName(name);

        return role.map(r -> new ResponseEntity<>(this.roleMapper.toRoleDTO(r), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/name/{name}")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable String name, @Validated @RequestBody RoleDTO roleDTO) {

        Role role = this.roleMapper.toRole(roleDTO);
        Role updatedRole = this.roleService.updateRole(name, role);
        RoleDTO updatedRoleDTO = this.roleMapper.toRoleDTO(updatedRole);

        return new ResponseEntity<>(updatedRoleDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        this.roleService.deleteRole(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{roleName}/permissions")
    public ResponseEntity<RoleDTO> addPermissionsToRoleByName(@PathVariable String roleName, @RequestBody Set<PermissionDTO> permissionNames) {

        Set<Permission> permissionSet = this.permissionMapper.permissionDTOListToPermissionList(permissionNames);
        Role updatedRole = this.roleService.addPermissionsToRoleByName(roleName, permissionSet);
        RoleDTO updatedRoleDTO = this.roleMapper.toRoleDTO(updatedRole);

        return new ResponseEntity<>(updatedRoleDTO, HttpStatus.OK);
    }
}

