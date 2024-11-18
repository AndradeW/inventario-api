package com.inventario.inventario_api.DTO.Mapper;

import com.inventario.inventario_api.DTO.UserDTO;
import com.inventario.inventario_api.DTO.UserInputDTO;
import com.inventario.inventario_api.model.UserEntity;
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

    public UserDTO userToUserDTO(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId(userEntity.getId());
        userDTO.setName(userEntity.getName());
        userDTO.setUsername(userEntity.getUsername());
        userDTO.setEmail(userEntity.getEmail());
        userDTO.setRole(userEntity.getRole());
        userDTO.setAddress(userEntity.getAddress());
        userDTO.setPhone(userEntity.getPhone());

        return userDTO;
    }

    public UserEntity userDTOToUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setId(userDTO.getId());
        userEntity.setName(userDTO.getName());
        userEntity.setUsername(userDTO.getUsername());
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setRole(userDTO.getRole());
        userEntity.setAddress(userDTO.getAddress());
        userEntity.setPhone(userDTO.getPhone());

        return userEntity;
    }

    public UserEntity userInputToUserInputDTO(UserInputDTO userInputDTO) {
        if (userInputDTO == null) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setName(userInputDTO.getName());
        userEntity.setUsername(userInputDTO.getUsername());
        userEntity.setEmail(userInputDTO.getEmail());
        userEntity.setPassword(encoder.encodePassword(userInputDTO.getPassword()));
        userEntity.setRole(userInputDTO.getRole());
        userEntity.setAddress(userInputDTO.getAddress());
        userEntity.setPhone(userInputDTO.getPhone());

        return userEntity;
    }
}
