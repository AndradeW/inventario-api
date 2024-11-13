package com.inventario.inventario_api.controller;

import com.inventario.inventario_api.model.User;
import com.inventario.inventario_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

//    // Create a new user
//    @PostMapping
//    public ResponseEntity<User> createUser(@RequestBody User user) {
//        User newUser = userService.saveUser(user);
//        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
//    }
//
    // Get all users
    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }
//
//    // Get a user by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<User> getUserById(@PathVariable Long id) {
//        Optional<User> user = userService.getUserById(id);
//        return user.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
//    }
//
//    // Update a user
//    @PutMapping("/{id}")
//    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
//        if (userService.getUserById(id).isPresent()) {
//            user.setId(id);
//            User updatedUser = userService.saveUser(user);
//            return ResponseEntity.ok(updatedUser);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//    }
//
//    // Delete a user
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//        if (userService.getUserById(id).isPresent()) {
//            userService.deleteUser(id);
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//    }

}
