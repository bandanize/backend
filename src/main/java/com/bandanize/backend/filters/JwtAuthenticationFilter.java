package com.bandanize.backend.filters;

import com.bandanize.backend.services.JwtService;
import com.bandanize.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        try {
            // Verifica si el encabezado contiene el token JWT
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                chain.doFilter(request, response);
                return;
            }

            jwt = authHeader.substring(7); // Extrae el token después de "Bearer "
            try {
                username = jwtService.extractUsername(jwt); // Extrae el username del token
            } catch (Exception e) {
                System.out.println("JWT Extraction failed: " + e.getMessage());
                chain.doFilter(request, response);
                return;
            }

            // Si el usuario no está autenticado, realiza la autenticación
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println("Processing JWT for user: " + username);

                UserDetails userDetails = userRepository.findByUsername(username)
                        .orElse(null);

                if (userDetails == null) {
                    System.out.println("User not found in DB: " + username);
                } else {
                    // Self-Healing
                    if (!userDetails.isEnabled()) {
                        System.out.println("User is disabled. Attempting self-healing for: " + username);
                        try {
                            com.bandanize.backend.models.UserModel userModel = (com.bandanize.backend.models.UserModel) userDetails;
                            userModel.setDisabled(false);
                            userRepository.save(userModel);
                            // Refresh userDetails
                            userDetails = userRepository.findByUsername(username).orElse(null);
                            if (userDetails == null) {
                                // Should not happen, but safe fallback
                                System.out.println("CRITICAL: User disappeared after save!");
                            }
                            System.out.println("SELF-HEALING SUCCESS: Re-enabled user " + username);
                        } catch (Exception e) {
                            System.out.println("SELF-HEALING FAILED: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    try {
                        if (jwtService.validateToken(jwt, userDetails)) {
                            System.out.println("Token Valid. Setting Auth for: " + username);
                            var authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        } else {
                            System.out.println("Token Validation Failed for: " + username);
                        }
                    } catch (Exception e) {
                        System.out.println("Token Validation Exception: " + e.getMessage());
                    }
                }
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            System.out.println("Filter Chain Exception: " + e.getMessage());
            e.printStackTrace();
            chain.doFilter(request, response);
        }
    }
}