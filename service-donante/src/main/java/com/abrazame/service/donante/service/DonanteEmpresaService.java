package com.abrazame.service.donante.service;

import com.abrazame.service.donante.model.DonanteEmpresa;
import com.abrazame.service.donante.repository.DonanteEmpresaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class DonanteEmpresaService {

    @Autowired
    private DonanteEmpresaRepository repository;

    public List<DonanteEmpresa> listarTodos() {
        return repository.findAll();
    }

    public Optional<DonanteEmpresa> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public DonanteEmpresa guardar(DonanteEmpresa empresa) {
        if (empresa.getRut() == null || empresa.getRut().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El RUT es obligatorio");
        }
        if (repository.findByRut(empresa.getRut()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Ya existe una empresa con el RUT: " + empresa.getRut());
        }
        return repository.save(empresa);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada");
        }
        repository.deleteById(id);
    }
}
