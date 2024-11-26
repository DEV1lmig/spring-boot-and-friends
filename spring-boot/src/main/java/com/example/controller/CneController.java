package com.example.controller;

import com.example.model.Cne;
import com.example.repository.CneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.example.exception.ResourceNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class CneController {
    @Autowired
    private CneRepository cneRepository;
    private final String FLASK_BASE_URL = "http://flask:5000";
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/buscar")
    public ResponseEntity<String> buscar(@RequestParam Map<String, String> queryParams,
            @RequestHeader("Authorization") String authHeader) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(FLASK_BASE_URL + "/buscar");
        if (queryParams.containsKey("cedula")) {
            builder.queryParam("cedula", queryParams.get("cedula"));
        }
        if (queryParams.containsKey("primer_nombre")) {
            builder.queryParam("primer_nombre", queryParams.get("primer_nombre"));
        }
        if (queryParams.containsKey("nombre_completo")) {
            builder.queryParam("nombre_completo", queryParams.get("nombre_completo"));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class);
    }

    @GetMapping("/cne")
    public ResponseEntity<List<Cne>> getAll(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    FLASK_BASE_URL + "/validate-token",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !Boolean.TRUE.equals(responseBody.get("valid"))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error de autenticación");
        }

        return ResponseEntity.ok(cneRepository.findAll());
    }

    @GetMapping("/cne/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        validateToken(authHeader);
        Cne cne = cneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CNE", "id", id));

        // Crear respuesta con datos y fotos relacionadas
        Map<String, Object> response = new HashMap<>();
        response.put("id", cne.getId());
        response.put("nacionalidad", cne.getNacionalidad());
        response.put("cedula", cne.getCedula());
        response.put("primer_apellido", cne.getPrimerApellido());
        response.put("segundo_apellido", cne.getSegundoApellido());
        response.put("primer_nombre", cne.getPrimerNombre());
        response.put("segundo_nombre", cne.getSegundoNombre());
        response.put("centro", cne.getCentro());
        response.put("nombre_completo", cne.getNombreCompleto());
        response.put("sexo", cne.getSexo());
        response.put("foto", cne.getFoto());
        response.put("huellas", cne.getHuellas());
        response.put("fotos", cne.getFotos()); // Incluir colección de fotos

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cne")
    public ResponseEntity<Cne> create(@Valid @RequestBody Cne cne,
            @RequestHeader("Authorization") String authHeader) {
        validateToken(authHeader);
        return ResponseEntity.status(201).body(cneRepository.save(cne));
    }

    @PutMapping("/cne/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody Cne cne,
            @RequestHeader("Authorization") String authHeader) {
        validateToken(authHeader);

        Cne existing = cneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CNE", "id", id));

        // Actualizar campos
        existing.setNacionalidad(cne.getNacionalidad());
        existing.setCedula(cne.getCedula());
        existing.setPrimerApellido(cne.getPrimerApellido());
        existing.setSegundoApellido(cne.getSegundoApellido());
        existing.setPrimerNombre(cne.getPrimerNombre());
        existing.setSegundoNombre(cne.getSegundoNombre());
        existing.setCentro(cne.getCentro());
        existing.setNombreCompleto(cne.getNombreCompleto());
        existing.setSexo(cne.getSexo());
        existing.setFoto(cne.getFoto());
        existing.setHuellas(cne.getHuellas());
        existing.setFotos(cne.getFotos());

        Cne updated = cneRepository.save(existing);

        // Construir respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("id", updated.getId());
        response.put("nacionalidad", updated.getNacionalidad());
        response.put("cedula", updated.getCedula());
        response.put("primer_apellido", updated.getPrimerApellido());
        response.put("segundo_apellido", updated.getSegundoApellido());
        response.put("primer_nombre", updated.getPrimerNombre());
        response.put("segundo_nombre", updated.getSegundoNombre());
        response.put("centro", updated.getCentro());
        response.put("nombre_completo", updated.getNombreCompleto());
        response.put("sexo", updated.getSexo());
        response.put("foto", updated.getFoto());
        response.put("huellas", updated.getHuellas());
        response.put("fotos", updated.getFotos());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/cne/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        validateToken(authHeader);
        return cneRepository.findById(id)
                .map(cne -> {
                    cneRepository.delete(cne);
                    return ResponseEntity.ok().build();
                })
                .orElseThrow(() -> new ResourceNotFoundException("CNE", "id", id));
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
