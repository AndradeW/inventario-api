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
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserInputDTO userInputDTO) {
        try {
            User s = this.userMapper.userInputToUser(userInputDTO);
            User savedUser = this.userService.saveUser(s);
            return new ResponseEntity<>(this.userMapper.userToUserDTO(savedUser), HttpStatus.CREATED);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get all users
    @GetMapping()
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<User> usersDTO = this.userService.getUsers();

        return ResponseEntity.ok(
                usersDTO.stream()
                        .map(this.userMapper::userToUserDTO)
                        .toList());
    }

    // Get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<User> user = this.userService.getUserById(id);

        return user.map(u -> ResponseEntity.ok(this.userMapper.userToUserDTO(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update a user
    @PutMapping("/{id}") //TODO implementar grupos de validación para diferenciarlos con las de Create
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserInputDTO userInputDTO) {

        try {
            if (this.userService.getUserById(id).isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User s = this.userMapper.userInputToUser(userInputDTO);
            s.setId(id);
            User updatedUser = this.userService.saveUser(s);
            return ResponseEntity.ok(this.userMapper.userToUserDTO(updatedUser));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    // Delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            if (this.userService.getUserById(id).isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            this.userService.deleteUser(id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
