package com.adan.web.proyecto.retoaws.service;

import com.adan.web.proyecto.retoaws.models.Persona;
import com.adan.web.proyecto.retoaws.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaServiceImpl implements IPersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Persona> findAll() {
        return (List<Persona>) personaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Persona> findById(Long id) {
        return personaRepository.findById(id);
    }

    @Override
    @Transactional
    public Persona save(Persona persona) {
        return personaRepository.save(persona);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        personaRepository.deleteById(id);
    }
}
