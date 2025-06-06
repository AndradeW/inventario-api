package com.inventario.inventario_api.repository;

import com.inventario.inventario_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

    boolean existsUserByUsername(String username);
    boolean existsUserByEmail(String email);
}
