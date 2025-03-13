package com.inventario.inventario_api.controller;

import com.inventario.inventario_api.DTO.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser() {
        return ResponseEntity.notFound().build();
    }
}
