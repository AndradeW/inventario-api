package com.inventario.inventario_api.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.inventario.inventario_api.Utils.JwtUtil;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Collection;

public class JwtTokenValidatorFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public JwtTokenValidatorFilter(HandlerExceptionResolver handlerExceptionResolver) {

        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                jwtToken = jwtToken.substring(7);

                DecodedJWT decodedJWT = this.jwtUtil.validateToken(jwtToken);

                String username = decodedJWT.getSubject();
                String stringAuthorities = this.jwtUtil.getSpecificClaim(decodedJWT, "authorities");

                Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(stringAuthorities);

                SecurityContext context = SecurityContextHolder.getContext();
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {

            this.handlerExceptionResolver.resolveException(request, response, null, e);

        }
    }
}
