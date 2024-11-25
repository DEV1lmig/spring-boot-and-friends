package com.example.controller;

import com.example.model.Cne;
import com.example.model.CneFotos;
import com.example.repository.CneRepository;
import com.example.repository.CneFotosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;
import java.util.List;
import org.springframework.web.client.RestTemplate;
import java.util.stream.Collectors;
import com.example.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api")
public class CneController {
    @Autowired
    private CneRepository cneRepository;
    private final String FLASK_BASE_URL = "http://flask:5000";
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/buscar")
    public ResponseEntity<String> buscar(@RequestParam Map<String, String> queryParams) {
        // Construir la URL para la búsqueda en Flask
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(FLASK_BASE_URL + "/buscar");

        // Agregar todos los parámetros de búsqueda disponibles
        if (queryParams.containsKey("cedula")) {
            builder.queryParam("cedula", queryParams.get("cedula"));
        }
        if (queryParams.containsKey("primer_nombre")) {
            builder.queryParam("primer_nombre", queryParams.get("primer_nombre"));
        }
        if (queryParams.containsKey("nombre_completo")) {
            builder.queryParam("nombre_completo", queryParams.get("nombre_completo"));
        }

        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);

        return ResponseEntity.ok(response.getBody());
    }

    @GetMapping("/cne")
    public ResponseEntity<String> getAll() {
        try {
            return restTemplate.getForEntity(FLASK_BASE_URL + "/cne", String.class);
        } catch (Exception e) {
            throw new ResourceNotFoundException("CNE", "operación", "getAll");
        }
    }


   @GetMapping("/cne/{id}")
    public ResponseEntity<String> getById(@PathVariable Long id) {
        try {
            return restTemplate.getForEntity(FLASK_BASE_URL + "/cne/" + id, String.class);
        } catch (Exception e) {
            throw new ResourceNotFoundException("CNE", "id", id);
        }
    }

    @PostMapping("/cne")
    public ResponseEntity<String> create(@RequestBody Cne cne) {
        try {
            return restTemplate.postForEntity(FLASK_BASE_URL + "/cne", cne, String.class);
        } catch (Exception e) {
            throw new ResourceNotFoundException("CNE", "operación", "create");
        }
    }

    @PutMapping("/cne/{id}")
    public ResponseEntity<String> actualizarCne(@PathVariable Long id, @RequestBody Cne cne) {
        try {
            return restTemplate.exchange(
                FLASK_BASE_URL + "/cne/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(cne),
                String.class
            );
        } catch (Exception e) {
            throw new ResourceNotFoundException("CNE", "id", id);
        }
    }

    @DeleteMapping("/cne/{id}")
    public ResponseEntity<String> eliminarCne(@PathVariable Long id) {
        try {
            return restTemplate.exchange(
                FLASK_BASE_URL + "/cne/" + id,
                HttpMethod.DELETE,
                null,
                String.class
            );
        } catch (Exception e) {
            throw new ResourceNotFoundException("CNE", "id", id);
        }
    }

    @RestController
@RequestMapping("/api/fotos")
public class CneFotosController {
    private final String FLASK_BASE_URL = "http://flask:5000";
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public ResponseEntity<String> getAll() {
        try {
            return restTemplate.getForEntity(FLASK_BASE_URL + "/fotos", String.class);
        } catch (Exception e) {
            throw new ResourceNotFoundException("CNE_FOTOS", "listado", "no disponible");
        }
    }

    @GetMapping("/cneFotos/{id}")
    public ResponseEntity<String> getById(@PathVariable Long id) {
        try {
            return restTemplate.getForEntity(FLASK_BASE_URL + "/fotos/" + id, String.class);
        } catch (Exception e) {
            throw new ResourceNotFoundException("CNE_FOTOS", "id", id);
        }
    }

    @PostMapping("/cneFotos")
    public ResponseEntity<String> create(@RequestBody CneFotos foto) {
        try {
            return restTemplate.postForEntity(FLASK_BASE_URL + "/fotos", foto, String.class);
        } catch (Exception e) {
            throw new ResourceNotFoundException("CNE_FOTOS", "creación", "fallida");
        }
    }

    @PutMapping("/cneFots/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody CneFotos foto) {
        try {
            return restTemplate.exchange(
                FLASK_BASE_URL + "/fotos/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(foto),
                String.class
            );
        } catch (Exception e) {
            throw new ResourceNotFoundException("CNE_FOTOS", "id", id);
        }
    }

    @DeleteMapping("/cneFotos/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            return restTemplate.exchange(
                FLASK_BASE_URL + "/fotos/" + id,
                HttpMethod.DELETE,
                null,
                String.class
            );
        } catch (Exception e) {
            throw new ResourceNotFoundException("CNE_FOTOS", "id", id);
        }
    }
}
}
