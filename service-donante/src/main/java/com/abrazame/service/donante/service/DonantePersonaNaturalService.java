package com.abrazame.service.donante.service;

import com.abrazame.service.donante.model.DonantePersonaNatural;
import com.abrazame.service.donante.repository.DonantePersonaNaturalRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class DonantePersonaNaturalService {

    @Autowired
    private DonantePersonaNaturalRepository repository;

    public List<DonantePersonaNatural> listarTodos() {
        return repository.findAll();
    }

    public Optional<DonantePersonaNatural> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public DonantePersonaNatural guardar(DonantePersonaNatural donante) {
        if (donante.getRut() == null || donante.getRut().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El RUT es obligatorio");
        }
        if (repository.findByRut(donante.getRut()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Ya existe un donante con el RUT: " + donante.getRut());
        }
        return repository.save(donante);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Donante no encontrado");
        }
        repository.deleteById(id);
    }
}
