package com.inventario.inventario_api.service;

import com.inventario.inventario_api.model.UserEntity;
import com.inventario.inventario_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Create or update user
    public UserEntity saveUser(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    // Get all users
    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Delete user by ID
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return User.builder()
                .username("johndoe")
                .password("$2y$10$fnoMh/rIiBIHTZUmmXG2qOBKajG81uM/NR6i4A/vrDd83zhurCK7a")
                .roles("ADMIN")
                .accountLocked(false)
                .disabled(false)
                .build();
    }


//        User userEntity = this.userRepository.findById(Long.valueOf(username))
//                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found."));
//
//        return User.builder()
//                .username(userEntity.getUsername())
//                .password(userEntity.getPassword())
//                .role("ADMIN")
//                .accountLocked(userEntity.isLocked())
//                .disabled(userEntity.isDisabled())
//                .build();
//    }
}
