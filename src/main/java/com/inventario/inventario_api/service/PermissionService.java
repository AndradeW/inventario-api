package com.inventario.inventario_api.service;

import com.inventario.inventario_api.model.Permission;
import com.inventario.inventario_api.repository.PermissionRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission createPermission(Permission permission) {

        Optional<Permission> permissionOptional = this.permissionRepository.findByName(permission.getName());

        if (permissionOptional.isPresent()) {
            throw new EntityExistsException("Permission " + permission.getName() + " already exists");
        }

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

    public Permission updatePermission(String name, Permission updatedPermission) {

        Optional<Permission> permissionOptional = this.permissionRepository.findByName(name);

        if (permissionOptional.isEmpty()) {
            throw new EntityNotFoundException("Permission with name " + name + " not found");
        }

        Permission permission = permissionOptional.get();
        permission.setName(updatedPermission.getName());

        return this.permissionRepository.save(permission);
    }

    public void deletePermission(String name) {

        Optional<Permission> permissionOptional = this.permissionRepository.findByName(name);
        if (permissionOptional.isEmpty()) {
            throw new EntityNotFoundException("Permission with name " + name + " not found");
        }

        this.permissionRepository.delete(permissionOptional.get());
    }
}
