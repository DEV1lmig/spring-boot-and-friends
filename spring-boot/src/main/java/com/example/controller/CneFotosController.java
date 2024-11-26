package com.example.controller;

import com.example.model.CneFotos;
import com.example.repository.CneFotosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.example.exception.ResourceNotFoundException;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/fotos")
public class CneFotosController {

    private static final String FLASK_BASE_URL = "http://flask:5000";

    @Autowired
    private CneFotosRepository cneFotosRepository;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<List<CneFotos>> getAll(@RequestHeader("Authorization") String authHeader) {
        try {
            validateToken(authHeader);
            List<CneFotos> fotos = cneFotosRepository.findAll();
            return ResponseEntity.ok(fotos);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving fotos");
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<CneFotos> getById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        try {
            validateToken(authHeader);
            CneFotos foto = cneFotosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CNE_FOTOS", "id", id));
            return ResponseEntity.ok(foto);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving foto");
        }
    }

    @PostMapping("/create")
    public ResponseEntity<CneFotos> create(@Valid @RequestBody CneFotos foto, @RequestHeader("Authorization") String authHeader) {
        try {
            validateToken(authHeader);
            CneFotos savedFoto = cneFotosRepository.save(foto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedFoto);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating foto");
        }
    }

    private void validateToken(String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }

        try {
            headers.set("Authorization", authHeader);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    FLASK_BASE_URL + "/validate-token",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {});

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !Boolean.TRUE.equals(responseBody.get("valid"))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error de autenticación: " + e.getMessage());
        }
    }
}
