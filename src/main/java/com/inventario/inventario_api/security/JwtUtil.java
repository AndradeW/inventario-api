package com.inventario.inventario_api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    private final String issuer;
    private final Algorithm ALGORITHM;

    public JwtUtil(@Value("${app.jwt.issuer}") String issuer, @Value("${app.jwt.secret_key}") String secretKey) {
        this.issuer = issuer;
        this.ALGORITHM = Algorithm.HMAC256(secretKey);
    }

    public String generateToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuer(this.issuer)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1))) // 1 hora
                .sign(this.ALGORITHM);
    }

    public boolean isValid(String token) {
        try {
            JWT.require(this.ALGORITHM)
                    .build()
                    .verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return JWT.require(this.ALGORITHM)
                .build()
                .verify(token)
                .getSubject();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
