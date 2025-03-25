package com.inventario.inventario_api.service;

import com.inventario.inventario_api.model.Permission;
import com.inventario.inventario_api.model.Role;
import com.inventario.inventario_api.repository.PermissionRepository;
import com.inventario.inventario_api.repository.RoleRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Transactional
    public Role createRole(Role role) {
        if (this.roleRepository.existsRoleByName(role.getName())) {
            throw new EntityExistsException("Role with name " + role.getName() + " already exists");
        }

        Set<Permission> assignedPermissions = new HashSet<>();
        Set<String> missingPermissions = new HashSet<>();

        for (Permission permission : role.getPermissions()) {
            this.permissionRepository.findByName(permission.getName())
                    .ifPresentOrElse(
                            assignedPermissions::add,
                            () -> missingPermissions.add(permission.getName()));
        }

        if (!missingPermissions.isEmpty()) {
            throw new EntityNotFoundException("Permissions not found: " + String.join(", ", missingPermissions));
        }

        role.setPermissions(assignedPermissions);

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
    public Role updateRole(String name, Role updatedRole) {
        Optional<Role> roleOptional = this.roleRepository.findByName(name);
        if (roleOptional.isEmpty()) {
            throw new EntityNotFoundException("Role with name " + name + " not found");
        }

        Role role = roleOptional.get();
        role.setName(updatedRole.getName());
        role.setDescription(updatedRole.getDescription());
        //role.setPermissionsList(updatedRole.getPermissionsList());

        return this.roleRepository.save(role);
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
                permissionOptional.ifPresent(permission -> role.getPermissions().add(permission));
            }
            return this.roleRepository.save(role);
        }
        return null;
    }

    @Transactional
    public Role addPermissionsToRoleByName(String roleName, Set<Permission> permissionSet) {
        // Buscar el rol por nombre
        Optional<Role> roleOptional = this.roleRepository.findByName(roleName);
        if (roleOptional.isEmpty()) {
            throw new EntityNotFoundException("Role with name " + roleName + " not found");
        }
        Role role = roleOptional.get();

        // Buscar y agregar los permisos al rol
        for (Permission permission : permissionSet) {
            Optional<Permission> permissionOptional = this.permissionRepository.findByName(permission.getName());
            if (permissionOptional.isPresent()) {
                role.getPermissions().add(permissionOptional.get());
            } else {
                throw new EntityNotFoundException("Permission with name " + permission.getName() + " not found");
            }
        }

        return this.roleRepository.save(role);
    }
}
