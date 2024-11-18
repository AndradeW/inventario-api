package com.inventario.inventario_api.service;

import com.inventario.inventario_api.model.UserEntity;
import com.inventario.inventario_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Create or update user
    public UserEntity saveUser(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    // Get all users
    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Delete user by ID
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
