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

        // Verifica si el encabezado contiene el token JWT
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // Extrae el token después de "Bearer "
        username = jwtService.extractUsername(jwt); // Extrae el username del token

        // Si el usuario no está autenticado, realiza la autenticación
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userRepository.findByUsername(username)
                    .orElse(null);

            if (userDetails != null) {
                // Self-Healing: If user was disabled by the Cascade bug, re-enable them
                if (!userDetails.isEnabled()) {
                    com.bandanize.backend.models.UserModel userModel = (com.bandanize.backend.models.UserModel) userDetails;
                    userModel.setDisabled(false);
                    userRepository.save(userModel);
                    // Reload to be sure
                    userDetails = userModel;
                    System.out.println("SELF-HEALING: Re-enabled user " + username);
                }

                if (jwtService.validateToken(jwt, userDetails)) {
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        chain.doFilter(request, response);
    }
}