package com.adan.web.proyecto.retoaws.controller;

import com.adan.web.proyecto.retoaws.models.Persona;
import com.adan.web.proyecto.retoaws.service.IPersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    @Autowired
    private IPersonaService personaService;

    @GetMapping
    public ResponseEntity<List<Persona>> listarPersonas() {
        List<Persona> personas = personaService.findAll();
        return new ResponseEntity<>(personas, HttpStatus.OK);
    }

    @PostMapping("/guardar")
    public ResponseEntity<Persona> guardarPersona(@Valid @RequestBody Persona persona) {
        Persona personaGuardada = personaService.save(persona);
        return new ResponseEntity<>(personaGuardada, HttpStatus.CREATED);
    }

    @GetMapping("/consultar/{id}")
    public ResponseEntity<Persona> consultarPersona(@PathVariable Long id) {
        Optional<Persona> persona = personaService.findById(id);
        return persona.map(ResponseEntity::ok)
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @GetMapping("/health")
    public ResponseEntity<Void> healthCheck() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleValidationExceptions(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}