package com.inventario.inventario_api.service;

import com.inventario.inventario_api.model.Permission;
import com.inventario.inventario_api.model.Role;
import com.inventario.inventario_api.repository.PermissionRepository;
import com.inventario.inventario_api.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public Role createRole(Role role) {
        return this.roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return this.roleRepository.findAll();
    }

    public Optional<Role> getRoleById(Long id) {
        return this.roleRepository.findById(id);
    }

    public Optional<Role> getRoleByName(String name) {
        return this.roleRepository.findByName(name);
    }

    @Transactional
    public Role updateRole(Long id, Role updatedRole) {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            role.setName(updatedRole.getName());
            role.setDescription(updatedRole.getDescription());
            role.setPermissionsList(updatedRole.getPermissionsList());
            return this.roleRepository.save(role);
        }
        return null;
    }

    @Transactional
    public void deleteRole(Long id) {
        this.roleRepository.deleteById(id);
    }

    // Agregar permisos a un rol
    public Role addPermissionsToRole(Long roleId, List<Long> permissionIds) {
        Optional<Role> roleOptional = this.roleRepository.findById(roleId);
        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            for (Long permissionId : permissionIds) {
                Optional<Permission> permissionOptional = this.permissionRepository.findById(permissionId);
                permissionOptional.ifPresent(permission -> role.getPermissionsList().add(permission));
            }
            return this.roleRepository.save(role);
        }
        return null;
    }
}
