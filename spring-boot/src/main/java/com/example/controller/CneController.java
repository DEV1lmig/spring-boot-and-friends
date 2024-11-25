package com.example.controller;

import com.example.model.Cne;
import com.example.model.CneFotos;
import com.example.repository.CneRepository;
import com.example.repository.CneFotosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.BeanUtils;
import java.util.Map;
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
            new ParameterizedTypeReference<Map<String, Object>>() {});

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
    public ResponseEntity<Cne> getById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        // Validar token con Flask primero
        try {
            restTemplate.exchange(
                FLASK_BASE_URL + "/buscar",
                HttpMethod.GET,
                entity,
                String.class
            );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }

        return ResponseEntity.ok(cneRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CNE", "id", id)));
    }

    @PostMapping("/cne")
    public ResponseEntity<Cne> create(@Valid @RequestBody Cne cne,
    @RequestHeader("Authorization") String authHeader) {
        validateToken(authHeader);
        return ResponseEntity.status(201).body(cneRepository.save(cne));
    }

    @PutMapping("/cne/{id}")
    public ResponseEntity<Cne> update(@PathVariable Long id, @Valid @RequestBody Cne cne,
    @RequestHeader("Authorization") String authHeader) {
        validateToken(authHeader);
        Cne existing = cneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CNE", "id", id));

        BeanUtils.copyProperties(cne, existing, "id");
        return ResponseEntity.ok(cneRepository.save(existing));
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

    @RestController
    @RequestMapping("/api/fotos")
    public class CneFotosController {

        @Autowired
        private CneFotosRepository cneFotosRepository;

        @GetMapping
        public ResponseEntity<List<CneFotos>> getAll(@RequestHeader("Authorization") String authHeader) {
            validateToken(authHeader);
            return ResponseEntity.ok(cneFotosRepository.findAll());
        }

        @GetMapping("/{id}")
        public ResponseEntity<CneFotos> getById(@PathVariable Long id,
        @RequestHeader("Authorization") String authHeader
        ) {
            validateToken(authHeader);
            return ResponseEntity.ok(cneFotosRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("CNE_FOTOS", "id", id)));
        }

        @PostMapping
        public ResponseEntity<CneFotos> create(@Valid @RequestBody CneFotos foto,
        @RequestHeader("Authorization") String authHeader) {
            validateToken(authHeader);
            return ResponseEntity.status(201).body(cneFotosRepository.save(foto));
        }

        @PutMapping("/{id}")
        public ResponseEntity<CneFotos> update(@PathVariable Long id, @Valid @RequestBody CneFotos foto,
        @RequestHeader("Authorization") String authHeader) {
            validateToken(authHeader);
            CneFotos existing = cneFotosRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("CNE_FOTOS", "id", id));

            BeanUtils.copyProperties(foto, existing, "id");
            return ResponseEntity.ok(cneFotosRepository.save(existing));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<?> delete(@PathVariable Long id,
        @RequestHeader("Authorization") String authHeader) {
            validateToken(authHeader);
            return cneFotosRepository.findById(id)
                    .map(foto -> {
                        cneFotosRepository.delete(foto);
                        return ResponseEntity.ok().build();
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("CNE_FOTOS", "id", id));
        }
    }

    private void validateToken(String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }

        try {
            headers.set("Authorization", authHeader); // Enviar el token completo como lo recibimos

            // Crear entidad HTTP
            HttpEntity<?> entity = new HttpEntity<>(headers);

            // Hacer la petición a Flask
            ResponseEntity<String> response = restTemplate.exchange(
                FLASK_BASE_URL + "/buscar", // Usar un endpoint que ya sabemos que funciona
                HttpMethod.GET,
                entity,
                String.class
            );

            // Si llegamos aquí, el token es válido
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
            }

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error de autenticación: " + e.getMessage());
        }
    }
}
