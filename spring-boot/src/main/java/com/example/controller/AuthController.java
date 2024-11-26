package com.example.controller;

import com.example.dto.AuthRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final String FLASK_BASE_URL = "http://flask:5000";
    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<AuthRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                FLASK_BASE_URL + "/auth/register",
                entity,
                String.class
            );

            return ResponseEntity.status(response.getStatusCode())
                               .body(response.getBody());

        } catch (HttpClientErrorException e) {
            // Propagar el mismo código de estado y mensaje de error de Flask
            return ResponseEntity.status(e.getStatusCode())
                               .body(e.getResponseBodyAsString());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<AuthRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                FLASK_BASE_URL + "/auth/login",
                entity,
                String.class
            );

            return ResponseEntity.status(response.getStatusCode())
                               .body(response.getBody());

        } catch (HttpClientErrorException e) {
            // Propagar el código 401 y mensaje de error cuando las credenciales son inválidas
            return ResponseEntity.status(e.getStatusCode())
                               .body(e.getResponseBodyAsString());
        }
    }
}
