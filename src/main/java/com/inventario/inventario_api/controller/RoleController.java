package com.inventario.inventario_api.controller;

import com.inventario.inventario_api.DTO.Mapper.RoleMapper;
import com.inventario.inventario_api.DTO.RoleDTO;
import com.inventario.inventario_api.model.Role;
import com.inventario.inventario_api.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @Autowired
    public RoleController(RoleService roleService, RoleMapper roleMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody RoleDTO roleDTO) {
        Role role = this.roleMapper.roleDTOToRole(roleDTO);
        return new ResponseEntity<>(this.roleService.createRole(role), HttpStatus.CREATED);
    }

    @GetMapping
    public List<Role> getAllRoles() {
        return this.roleService.getAllRoles();
    }

    @GetMapping("/{id}")
    public Optional<Role> getRoleById(@PathVariable Long id) {
        return this.roleService.getRoleById(id);
    }

    @GetMapping("/name/{name}")
    public Optional<Role> getRoleByName(@PathVariable String name) {
        return this.roleService.getRoleByName(name);
    }

    @PutMapping("/name/{name}")
    public ResponseEntity<Role> updateRole(@PathVariable String name, @Validated @RequestBody Role role) {
        return new ResponseEntity<>(this.roleService.updateRole(name, role), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        this.roleService.deleteRole(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{roleName}/permissions")
    public ResponseEntity<Role> addPermissionsToRoleByName(@PathVariable String roleName, @RequestBody List<String> permissionNames) {
        return new ResponseEntity<>(this.roleService.addPermissionsToRoleByName(roleName, permissionNames), HttpStatus.OK);
    }
}

