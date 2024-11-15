package com.inventario.inventario_api.controller;

import com.inventario.inventario_api.DTO.Mapper.UserMapper;
import com.inventario.inventario_api.DTO.UserDTO;
import com.inventario.inventario_api.DTO.UserInputDTO;
import com.inventario.inventario_api.model.User;
import com.inventario.inventario_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    // Create a new user
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserInputDTO userInputDTO) {
        try {
            User s = userMapper.userInputToUserInputDTO(userInputDTO);
            User newUser = userService.saveUser(s);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get all users
    @GetMapping()
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<User> usersDTO = userService.getUsers();
        if (usersDTO.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(
                usersDTO.stream().map(userMapper::userToUserDTO).collect(Collectors.toList()));
    }

    // Get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);

        if (user.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(userMapper.userToUserDTO(user.get()));
    }

    // Update a user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {

        if (userService.getUserById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        user.setId(id);
        User updatedUser = userService.saveUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    // Delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            if (userService.getUserById(id).isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            userService.deleteUser(id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
