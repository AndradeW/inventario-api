package com.inventario.inventario_api.controller;

import com.inventario.inventario_api.model.Permission;
import com.inventario.inventario_api.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping
    public Permission createPermission(@RequestBody Permission permission) {
        return this.permissionService.createPermission(permission);
    }

    @GetMapping
    public List<Permission> getAllPermissions() {
        return this.permissionService.getAllPermissions();
    }

    @GetMapping("/{id}")
    public Optional<Permission> getPermissionById(@PathVariable Long id) {
        return this.permissionService.getPermissionById(id);
    }

    @GetMapping("/name/{name}")
    public Optional<Permission> getPermissionByName(@PathVariable String name) {
        return this.permissionService.getPermissionByName(name);
    }

    @PutMapping("/{id}")
    public Permission updatePermission(@PathVariable Long id, @RequestBody Permission permission) {
        return this.permissionService.updatePermission(id, permission);
    }

    @DeleteMapping("/{id}")
    public void deletePermission(@PathVariable Long id) {
        this.permissionService.deletePermission(id);
    }
}
