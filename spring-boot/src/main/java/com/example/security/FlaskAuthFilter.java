package com.example.security;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.core.ParameterizedTypeReference;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class FlaskAuthFilter extends OncePerRequestFilter {
    private final String FLASK_AUTH_URL = "http://flask:5000/validate-token";
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);

        if (token != null) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + token);
                HttpEntity<?> entity = new HttpEntity<>(headers);

                ResponseEntity<Map<String, Object>> validationResponse = restTemplate.exchange(
                    FLASK_AUTH_URL,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
                );

                Map<String, Object> responseBody = validationResponse.getBody();
                if (responseBody != null && Boolean.TRUE.equals(responseBody.get("valid"))) {
                    // Crear autenticación
                    String role = (String) responseBody.get("role");
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())
                    );

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(null, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
