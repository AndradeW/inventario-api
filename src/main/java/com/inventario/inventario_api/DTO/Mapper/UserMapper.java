package com.inventario.inventario_api.DTO.Mapper;

import com.inventario.inventario_api.DTO.UserDTO;
import com.inventario.inventario_api.DTO.UserInputDTO;
import com.inventario.inventario_api.model.Role;
import com.inventario.inventario_api.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
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

    default Set<Role> mapRolesToSet(String[] role) {
        HashSet<Role> roles = new HashSet<>();

        if (role == null || role.length == 0) {
            Role userRole = new Role();
            userRole.setName(Role.ROLE_CUSTOMER);
            return Set.of(userRole);
        }

        for (String roleName : role) {
            Role userRole = new Role();
            userRole.setName(roleName.toUpperCase());
            roles.add(userRole);
        }

        return roles;
    }

    @Named("encryptPassword")
    default String mapPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    default String[] mapRolesToString(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return new String[0];
        }

        return roles.stream()
                .map(Role::getName)
                .sorted()
                .toArray(String[]::new);
    }
}
