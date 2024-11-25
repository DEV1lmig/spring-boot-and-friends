package com.example.controller;

import com.example.model.Cne;
import com.example.model.CneFotos;
import com.example.repository.CneRepository;
import com.example.repository.CneFotosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.BeanUtils;
import java.util.Map;
import java.util.List;
import org.springframework.web.client.RestTemplate;
import com.example.exception.ResourceNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class CneController {
    @Autowired
    private CneRepository cneRepository;
    private final String FLASK_BASE_URL = "http://flask:5000";
    private final RestTemplate restTemplate = new RestTemplate();

    // Mantener la búsqueda en Flask
    @GetMapping("/buscar")
    public ResponseEntity<String> buscar(@RequestParam Map<String, String> queryParams) {
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
        return restTemplate.getForEntity(builder.toUriString(), String.class);
    }

    @GetMapping("/cne")
    public ResponseEntity<List<Cne>> getAll() {
        return ResponseEntity.ok(cneRepository.findAll());
    }

    @GetMapping("/cne/{id}")
    public ResponseEntity<Cne> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CNE", "id", id)));
    }

    @PostMapping("/cne")
    public ResponseEntity<Cne> create(@Valid @RequestBody Cne cne) {
        return ResponseEntity.status(201).body(cneRepository.save(cne));
    }

    @PutMapping("/cne/{id}")
    public ResponseEntity<Cne> update(@PathVariable Long id, @Valid @RequestBody Cne cne) {
        Cne existing = cneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CNE", "id", id));

        BeanUtils.copyProperties(cne, existing, "id");
        return ResponseEntity.ok(cneRepository.save(existing));
    }

    @DeleteMapping("/cne/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
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
        public ResponseEntity<List<CneFotos>> getAll() {
            return ResponseEntity.ok(cneFotosRepository.findAll());
        }

        @GetMapping("/{id}")
        public ResponseEntity<CneFotos> getById(@PathVariable Long id) {
            return ResponseEntity.ok(cneFotosRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("CNE_FOTOS", "id", id)));
        }

        @PostMapping
        public ResponseEntity<CneFotos> create(@Valid @RequestBody CneFotos foto) {
            return ResponseEntity.status(201).body(cneFotosRepository.save(foto));
        }

        @PutMapping("/{id}")
        public ResponseEntity<CneFotos> update(@PathVariable Long id, @Valid @RequestBody CneFotos foto) {
            CneFotos existing = cneFotosRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("CNE_FOTOS", "id", id));

            BeanUtils.copyProperties(foto, existing, "id");
            return ResponseEntity.ok(cneFotosRepository.save(existing));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<?> delete(@PathVariable Long id) {
            return cneFotosRepository.findById(id)
                    .map(foto -> {
                        cneFotosRepository.delete(foto);
                        return ResponseEntity.ok().build();
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("CNE_FOTOS", "id", id));
        }
    }
}
