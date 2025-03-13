package com.inventario.inventario_api.DTO.Mapper;

import com.inventario.inventario_api.DTO.UserDTO;
import com.inventario.inventario_api.DTO.UserInputDTO;
import com.inventario.inventario_api.model.User;
import com.inventario.inventario_api.security.BCryptEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final BCryptEncoder encoder;

    @Autowired
    public UserMapper(BCryptEncoder encoder) {
        this.encoder = encoder;
    }

    public UserDTO userToUserDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        userDTO.setAddress(user.getAddress());
        userDTO.setPhone(user.getPhone());

        return userDTO;
    }

    public User userDTOToUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();

        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setAddress(userDTO.getAddress());
        user.setPhone(userDTO.getPhone());

        return user;
    }

    public User userInputToUser(UserInputDTO userInputDTO) {
        if (userInputDTO == null) {
            return null;
        }

        User user = new User();

        user.setName(userInputDTO.getName());
        user.setUsername(userInputDTO.getUsername());
        user.setEmail(userInputDTO.getEmail());
        user.setPassword(this.encoder.encodePassword(userInputDTO.getPassword()));
        user.setRole(userInputDTO.getRole());
        user.setAddress(userInputDTO.getAddress());
        user.setPhone(userInputDTO.getPhone());

        return user;
    }
}
