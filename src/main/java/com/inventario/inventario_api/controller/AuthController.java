package com.inventario.inventario_api.controller;

import com.inventario.inventario_api.DTO.AuthResponse;
import com.inventario.inventario_api.DTO.Mapper.UserMapper;
import com.inventario.inventario_api.DTO.UserDTO;
import com.inventario.inventario_api.DTO.UserInputDTO;
import com.inventario.inventario_api.DTO.UserLoginDTO;
import com.inventario.inventario_api.model.User;
import com.inventario.inventario_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public AuthController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserInputDTO userInputDTO) {

        User s = this.userMapper.userInputToUser(userInputDTO);
        User savedUser = this.userService.createUser(s);
        return new ResponseEntity<>(this.userMapper.userToUserDTO(savedUser), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {

        return new ResponseEntity<>(this.userService.loginUser(userLoginDTO), HttpStatus.OK);

    }
}
