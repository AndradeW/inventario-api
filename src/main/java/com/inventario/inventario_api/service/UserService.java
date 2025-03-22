package com.inventario.inventario_api.service;

import com.inventario.inventario_api.DTO.AuthResponse;
import com.inventario.inventario_api.DTO.UserLoginDTO;
import com.inventario.inventario_api.Utils.JwtUtil;
import com.inventario.inventario_api.model.Role;
import com.inventario.inventario_api.model.User;
import com.inventario.inventario_api.repository.RolesRepository;
import com.inventario.inventario_api.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;

    @Autowired
    public UserService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, RolesRepository rolesRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.rolesRepository = rolesRepository;
    }

    // Create or update user
    public User updateUser(User user) {
        return this.userRepository.save(user);
    }

    // Create or update user
    public User createUser(User user) {

        if (this.userRepository.existsUserByUsername(user.getUsername())) {
            throw new BadCredentialsException("Username already exists");
        }

        if (this.userRepository.existsUserByEmail(user.getEmail())) {
            throw new BadCredentialsException("Email already exists");
        }

        Map<String, Role> rolesDb = this.rolesRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Role::getName, role -> role));

        Set<Role> assignedRoles = new HashSet<>();
        Set<String> missingRoles = new HashSet<>();

        for (Role role : user.getRoles()) {
            Role roleFromDb = rolesDb.get(role.getName());
            if (roleFromDb != null) {
                assignedRoles.add(roleFromDb);
            } else {
                missingRoles.add(role.getName());
            }
        }

        if (!missingRoles.isEmpty()) {
            throw new BadCredentialsException("Roles do not match: " + String.join(", ", missingRoles));
        }

        user.setRoles(assignedRoles);

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

    public AuthResponse loginUser(@Valid UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.username();
        String password = userLoginDTO.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = this.jwtUtil.createToken(authentication);

        return new AuthResponse(username, "", accessToken, true);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);

        if (!this.passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Contraseña incorrecta");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }
}
