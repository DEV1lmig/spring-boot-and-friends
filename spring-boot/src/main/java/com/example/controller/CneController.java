package com.example.controller;

import com.example.model.Cne;
import com.example.model.CneFotos;
import com.example.repository.CneRepository;
import com.example.repository.CneFotosRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final String FLASK_BASE_URL = "http://localhost:5000";

    @GetMapping("/buscar")
      public ResponseEntity<String> buscar(@RequestParam Map<String, String> queryParams) {
          // Obtén los datos necesarios de la base de datos
          String cedula = (String) queryParams.get("cedula");
          String primerNombre = (String) queryParams.get("primer_nombre");
          String nombreCompleto = (String) queryParams.get("nombre_completo");

          // Construir la URL para la búsqueda en Flask
          UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(FLASK_BASE_URL + "/buscar");
          if (cedula != null) builder.queryParam("cedula", cedula);
          if (primerNombre != null) builder.queryParam("primer_nombre", primerNombre);
          if (nombreCompleto != null) builder.queryParam("nombre_completo", nombreCompleto);

          // Realizar la solicitud a Flask
          RestTemplate restTemplate = new RestTemplate();
          ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);

          // Devolver la respuesta de Flask al usuario
          return ResponseEntity.ok(response.getBody());
      }

      @GetMapping
      public List<Cne> getAll() {
          return cneRepository.findAll().stream().map(cne -> (Cne) cne).collect(Collectors.toList());
      }

      @PostMapping
      public Cne create(@RequestBody Cne cne) {
          return cneRepository.save(cne);
      }

      @GetMapping("/{id}")
      public Cne getById(@PathVariable Long id) {
          return cneRepository.findById(id).orElseThrow(() ->
              new ResourceNotFoundException("Cne not found with id " + id));
      }
      public class CneFotosController {
        @Autowired
        private CneFotosRepository cneFotosRepository;

        @GetMapping
        public List<CneFotos> getAll() {
            return cneFotosRepository.findAll();
        }

        @PostMapping
        public CneFotos create(@RequestBody CneFotos cneFotos) {
            return (CneFotos) cneFotosRepository.save(cneFotos);
        }

        @GetMapping("/{id}")
        public CneFotos getById(@PathVariable Long id) {
            return cneFotosRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("CneFotos not found with id " + id));
        }

        // Métodos adicionales para update y delete
        @PutMapping("/cne/{id}")
        public Cne actualizarCne(@PathVariable Long id, @RequestBody Cne detallesCne) {
            Cne cne = cneRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("CNE no encontrado con id: " + id));

            cne.setNacionalidad(detallesCne.getNacionalidad());
            cne.setCedula(detallesCne.getCedula());
            cne.setPrimerApellido(detallesCne.getPrimerApellido());
            cne.setSegundoApellido(detallesCne.getSegundoApellido());
            cne.setPrimerNombre(detallesCne.getPrimerNombre());
            cne.setSegundoNombre(detallesCne.getSegundoNombre());
            cne.setCentro(detallesCne.getCentro());
            cne.setNombreCompleto(detallesCne.getNombreCompleto());
            cne.setSexo(detallesCne.getSexo());
            cne.setFoto(detallesCne.getFoto());
            cne.setHuellas(detallesCne.getHuellas());

            return cneRepository.save(cne);
        }

        // Método para eliminar un CNE existente
        @DeleteMapping("/cne/{id}")
        public ResponseEntity<?> eliminarCne(@PathVariable Long id) {
            Cne cne = cneRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("CNE no encontrado con id: " + id));

            cneRepository.delete(cne);

            return ResponseEntity.ok().build();
        }
      }
    }
