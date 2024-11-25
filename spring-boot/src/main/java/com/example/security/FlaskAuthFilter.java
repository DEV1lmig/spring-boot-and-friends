package com.example.security;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.lang.NonNull;

public class FlaskAuthFilter extends OncePerRequestFilter {
    private final String FLASK_AUTH_URL = "http://flask:5000/validate-token";
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) {
        String token = extractToken(request);

        if (token != null) {
            try {
                ResponseEntity<Map<String, Object>> validationResponse = restTemplate.exchange(
                    FLASK_AUTH_URL,
                    org.springframework.http.HttpMethod.POST,
                    new org.springframework.http.HttpEntity<>(Map.of("token", token)),
                    new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
                );

                Map<String, Object> responseBody = validationResponse.getBody();
                if (responseBody != null && Boolean.TRUE.equals(responseBody.get("valid"))) {
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
