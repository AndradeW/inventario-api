package com.inventario.inventario_api.DTO.Mapper;

import com.inventario.inventario_api.DTO.UserDTO;
import com.inventario.inventario_api.DTO.UserInputDTO;
import com.inventario.inventario_api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final PasswordEncoder encoder;

    @Autowired
    public UserMapper(PasswordEncoder encoder) {
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
        user.setAddress(userDTO.getAddress());
        user.setPhone(userDTO.getPhone());

        return user;
    }

    public User userInputToUser(UserInputDTO userInputDTO) {
        if (userInputDTO == null) {
            return null;
        }

        User user = new User();

        user.setUsername(userInputDTO.getUsername());
        user.setEmail(userInputDTO.getEmail());
        user.setPassword(this.encoder.encode(userInputDTO.getPassword()));

        user.setName(userInputDTO.getName());
        user.setAddress(userInputDTO.getAddress());
        user.setPhone(userInputDTO.getPhone());

        user.setEnabled(true);
        user.setAccountNoExpired(true);
        user.setCredentialsNoExpired(true);
        user.setAccountNoLocked(true);

        return user;
    }
}
