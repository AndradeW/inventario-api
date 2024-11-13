package com.inventario.inventario_api.service;

import com.inventario.inventario_api.model.User;
import com.inventario.inventario_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    // Dependency injection of the repository
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    // Create or update user
//    public User saveUser(User user) {
//        return userRepository.save(user);
//    }
//
    // Get all users
    public List<User> getUsers() {
        return userRepository.findAll();
    }
//
//    // Get user by ID
//    public Optional<User> getUserById(Long id) {
//        return userRepository.findById(id);
//    }
//
//    // Delete user by ID
//    public void deleteUser(Long id) {
//        userRepository.deleteById(id);
//    }
}
