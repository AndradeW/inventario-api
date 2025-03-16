package com.inventario.inventario_api.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.inventario.inventario_api.exceptions.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${security.jwt.key.private}")
    private String privateKey;
    @Value("${security.jwt.key.generator}")
    private String userGenerator;

    public String createToken(Authentication authentication) {

        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        String username = authentication.getPrincipal().toString();

        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String jwtToken = JWT.create()
                .withIssuer(this.userGenerator)
                .withSubject(username)
                .withClaim("authorities", authorities)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(new Date().getTime() + TimeUnit.MINUTES.toMillis(30)))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);

        return jwtToken;

    }

    public DecodedJWT validateToken(String token) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();

            return verifier.verify(token);

        } catch (TokenExpiredException e) {
            throw new InvalidTokenException("El token ha expirado", e);
        } catch (JWTVerificationException e) {
            throw new InvalidTokenException("Token inválido", e);
        }
    }

    public String extractUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim("username").asString();
    }

    public String getSpecificClaim(DecodedJWT decodedJWT, String claim) {
        return decodedJWT.getClaim(claim).asString();
    }

    public Map<String, Claim> getAllClaimsFromToken(DecodedJWT decodedJWT) {
        return decodedJWT.getClaims();
    }
}
