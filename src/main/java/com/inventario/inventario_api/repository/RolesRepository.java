package com.inventario.inventario_api.repository;

import com.inventario.inventario_api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Role, Long> {

}
