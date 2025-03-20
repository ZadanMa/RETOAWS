package com.adan.web.proyecto.retoaws.service;

import com.adan.web.proyecto.retoaws.models.Persona;
import java.util.List;
import java.util.Optional;

public interface IPersonaService {
    List<Persona> findAll();
    Optional<Persona> findById(Long id);
    Persona save(Persona persona);
    void deleteById(Long id);
}