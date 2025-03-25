package com.inventario.inventario_api.controller;

import com.inventario.inventario_api.DTO.Mapper.PermissionMapper;
import com.inventario.inventario_api.DTO.PermissionDTO;
import com.inventario.inventario_api.model.Permission;
import com.inventario.inventario_api.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;
    private final PermissionMapper permissionMapper;

    @Autowired
    public PermissionController(PermissionService permissionService, PermissionMapper permissionMapper) {
        this.permissionService = permissionService;
        this.permissionMapper = permissionMapper;
    }

    @PostMapping
    public ResponseEntity<Permission> createPermission(@RequestBody PermissionDTO permissionDTO) {
        Permission permission = this.permissionMapper.toPermission(permissionDTO);
        return ResponseEntity.ok((this.permissionService.createPermission(permission)));
    }

    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissions() {
        List<Permission> permission = this.permissionService.getAllPermissions();
        return ResponseEntity.ok(permission);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permission> getPermissionById(@PathVariable Long id) {

        Optional<Permission> permission = this.permissionService.getPermissionById(id);

        return permission.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Permission> getPermissionByName(@PathVariable String name) {
        Optional<Permission> permission = this.permissionService.getPermissionByName(name);

        return permission.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Permission> updatePermission(@PathVariable Long id, @RequestBody PermissionDTO permissionDTO) {

        Permission permission = this.permissionMapper.toPermission(permissionDTO);
        return ResponseEntity.ok(this.permissionService.updatePermission(id, permission));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        this.permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}
