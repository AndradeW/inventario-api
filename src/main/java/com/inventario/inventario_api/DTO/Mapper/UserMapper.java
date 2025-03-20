package com.inventario.inventario_api.DTO.Mapper;

import com.inventario.inventario_api.DTO.UserDTO;
import com.inventario.inventario_api.DTO.UserInputDTO;
import com.inventario.inventario_api.model.Roles;
import com.inventario.inventario_api.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isEnabled", constant = "true")
    @Mapping(target = "accountNoExpired", constant = "true")
    @Mapping(target = "accountNoLocked", constant = "true")
    @Mapping(target = "credentialsNoExpired", constant = "true")
    @Mapping(target = "roles", expression = "java(mapRolesToSet(userInputDTO.getRole()))")
    @Mapping(target = "password", qualifiedByName = "encryptPassword")
    User userInputDTOToUser(UserInputDTO userInputDTO);

    @Mapping(target = "role", expression = "java(mapRolesToString(user.getRoles()))")
    UserDTO userToUserDTO(User user);

    default Set<Roles> mapRolesToSet(String[] role) {
        if (role == null || role.length == 0) {
            return Set.of();
        }

        Roles userRole = new Roles();
        Arrays.stream(role).forEach(userRole::setName);

        return Set.of(userRole);
    }

    @Named("encryptPassword")
    default String mapPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    default String[] mapRolesToString(Set<Roles> roles) {
        if (roles == null || roles.isEmpty()) {
            return new String[0];
        }
        return roles.stream().map(r -> r.getRole().name()).toArray(String[]::new);
    }
}
