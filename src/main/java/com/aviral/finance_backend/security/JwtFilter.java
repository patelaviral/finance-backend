package com.aviral.finance_backend.security;

import com.aviral.finance_backend.repository.UserRepository;
import io.jsonwebtoken.io.IOException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain)
            throws ServletException, IOException, java.io.IOException {

        String path = request.getRequestURI();

        if(path.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if(authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            if(jwtUtil.isTknValid(token)) {
                String email = jwtUtil.extractEmail(token);

                userRepository.findByEmail(email)
                        .ifPresent(user -> request.setAttribute("user", user));
            }
        }

        filterChain.doFilter(request, response);
    }
}
