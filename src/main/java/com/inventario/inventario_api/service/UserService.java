package com.inventario.inventario_api.service;

import com.inventario.inventario_api.DTO.AuthResponse;
import com.inventario.inventario_api.DTO.UserLoginDTO;
import com.inventario.inventario_api.Utils.JwtUtil;
import com.inventario.inventario_api.model.User;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
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
        System.out.println("username " + username + " password " + password);

        if(userDetails == null) {
            System.out.println("Usuario no existe");
            throw new UsernameNotFoundException("El usuario " + username + " no existe.");
        }

        if (!this.passwordEncoder.matches(password, userDetails.getPassword())) {
            System.out.println("Cntraseña incorrecta");
            throw new BadCredentialsException("Contraseña incorrecta");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }
}
