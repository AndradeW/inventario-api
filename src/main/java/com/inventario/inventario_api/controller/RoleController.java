package com.inventario.inventario_api.controller;

import com.inventario.inventario_api.model.Role;
import com.inventario.inventario_api.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public Role createRole(@RequestBody Role role) {
        return this.roleService.createRole(role);
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

    @PutMapping("/{id}")
    public Role updateRole(@PathVariable Long id, @RequestBody Role role) {
        return this.roleService.updateRole(id, role);
    }

    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable Long id) {
        this.roleService.deleteRole(id);
    }

    @PostMapping("/{roleId}/permissions")
    public Role addPermissionsToRole(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        return this.roleService.addPermissionsToRole(roleId, permissionIds);
    }
}

