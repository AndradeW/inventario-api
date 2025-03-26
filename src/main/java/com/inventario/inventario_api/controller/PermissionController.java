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
import java.util.Set;

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
    public ResponseEntity<PermissionDTO> createPermission(@RequestBody PermissionDTO permissionDTO) {
        Permission permission = this.permissionMapper.toPermission(permissionDTO);
        Permission createPermission = this.permissionService.createPermission(permission);
        PermissionDTO creatPermissionDTO = this.permissionMapper.toPermissionDTO(createPermission);
        return ResponseEntity.ok(creatPermissionDTO);
    }

    @GetMapping
    public ResponseEntity<Set<PermissionDTO>> getAllPermissions() {
        List<Permission> permission = this.permissionService.getAllPermissions();

        return ResponseEntity.ok(this.permissionMapper.tooPermissionDTOList(permission));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionDTO> getPermissionById(@PathVariable Long id) {

        Optional<Permission> permission = this.permissionService.getPermissionById(id);

        return permission.map(r -> ResponseEntity.ok(this.permissionMapper.toPermissionDTO(r)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PermissionDTO> getPermissionByName(@PathVariable String name) {
        Optional<Permission> permission = this.permissionService.getPermissionByName(name);

        return permission.map(r -> ResponseEntity.ok(this.permissionMapper.toPermissionDTO(r)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionDTO> updatePermission(@PathVariable Long id, @RequestBody PermissionDTO permissionDTO) {

        Permission permission = this.permissionMapper.toPermission(permissionDTO);
        Permission updatedPermission = this.permissionService.updatePermission(id, permission);
        PermissionDTO updatedPermissionDTO = this.permissionMapper.toPermissionDTO(updatedPermission);

        return ResponseEntity.ok(updatedPermissionDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        this.permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}
