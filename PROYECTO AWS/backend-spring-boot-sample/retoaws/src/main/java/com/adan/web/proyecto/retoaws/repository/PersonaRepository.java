package com.adan.web.proyecto.retoaws.repository;

import com.adan.web.proyecto.retoaws.models.Persona;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends CrudRepository<Persona, Long> {
}