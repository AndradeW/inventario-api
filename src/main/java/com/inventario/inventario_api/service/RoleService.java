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

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public Role createRole(Role role) {
        Optional<Role> roleOptional = this.roleRepository.findByName(role.getName());
        if (roleOptional.isPresent()) {
            throw new EntityExistsException("Role with name " + role.getName() + " ya existe");
        }

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
        if (roleOptional.isEmpty()) {
            throw new EntityNotFoundException("Role with id " + id + " not found");
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
                permissionOptional.ifPresent(permission -> role.getPermissionsList().add(permission));
            }
            return this.roleRepository.save(role);
        }
        return null;
    }

    @Transactional
    public Role addPermissionsToRoleByName(String roleName, List<String> permissionNames) {
        // Buscar el rol por nombre
        Optional<Role> roleOptional = this.roleRepository.findByName(roleName);
        if (roleOptional.isEmpty()) {
            throw new EntityNotFoundException("Role with name " + roleName + " not found");
        }
        Role role = roleOptional.get();

        // Buscar y agregar los permisos al rol
        for (String permissionName : permissionNames) {
            Optional<Permission> permissionOptional = this.permissionRepository.findByName(permissionName);
            if (permissionOptional.isPresent()) {
                Permission permission = permissionOptional.get();
                role.getPermissionsList().add(permission);
            } else {
                throw new EntityNotFoundException("Permission with name " + permissionName + " not found");
            }
        }

        return this.roleRepository.save(role);
    }
}
