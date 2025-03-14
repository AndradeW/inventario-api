package com.inventario.inventario_api.service;

import com.inventario.inventario_api.model.User;
import com.inventario.inventario_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    // Get all users
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    // Get user by ID
    public Optional<User> getUserById(Long id) {
        return this.userRepository.findById(id);
    }

    // Delete user by ID
    public void deleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = this.userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe."));

        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();

        user.getRoles()
                .forEach(role -> simpleGrantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role)));

        user.getRoles().stream()
                .flatMap(role -> role.getPermissionsList().stream())
                .forEach(permission -> simpleGrantedAuthorities.add(new SimpleGrantedAuthority(permission.getName())));


        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                user.isAccountNoExpired(),
                user.isCredentialsNoExpired(),
                user.isAccountNoLocked(),
                simpleGrantedAuthorities);
    }
}
