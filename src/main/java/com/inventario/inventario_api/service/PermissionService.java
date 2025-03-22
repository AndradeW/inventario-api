package com.inventario.inventario_api.service;

import com.inventario.inventario_api.model.Permission;
import com.inventario.inventario_api.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public Permission createPermission(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public List<Permission> getAllPermissions() {
        return this.permissionRepository.findAll();
    }

    public Optional<Permission> getPermissionById(Long id) {
        return this.permissionRepository.findById(id);
    }

    public Optional<Permission> getPermissionByName(String name) {
        return this.permissionRepository.findByName(name);
    }

    public Permission updatePermission(Long id, Permission updatedPermission) {
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        if (permissionOptional.isPresent()) {
            Permission permission = permissionOptional.get();
            permission.setName(updatedPermission.getName());
            return this.permissionRepository.save(permission);
        }
        return null;
    }

    public void deletePermission(Long id) {
        this.permissionRepository.deleteById(id);
    }
}
