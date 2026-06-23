package com.abrazame.service.donante.service;

import com.abrazame.service.donante.model.Voluntario;
import com.abrazame.service.donante.repository.VoluntarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class VoluntarioService {

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    public List<Voluntario> listarTodos() {
        return voluntarioRepository.findAll();
    }

    public Voluntario guardarDirecto(Voluntario v) { return voluntarioRepository.save(v); }

    public Optional<Voluntario> buscarPorCorreo(String correo) { return voluntarioRepository.findByCorreoElectronico(correo); }

    public Optional<Voluntario> buscarPorId(Long id) {
        return voluntarioRepository.findById(id);
    }

    @Transactional
    public Voluntario guardar(Voluntario voluntario) {
        if (voluntarioRepository.findByRut(voluntario.getRut()).isPresent()) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Ya existe un voluntario con el RUT: " + voluntario.getRut()
            );
        }
        // Vincular relación bidireccional
        if (voluntario.getPerfilVoluntario() != null) {
            voluntario.getPerfilVoluntario().setVoluntario(voluntario);
        }
        return voluntarioRepository.save(voluntario);
    }

    @Transactional
    public Voluntario actualizar(Long id, Voluntario datos) {
        Voluntario existente = voluntarioRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voluntario no encontrado"));

        existente.setPrimerNombre(datos.getPrimerNombre());
        existente.setSegundoNombre(datos.getSegundoNombre());
        existente.setApellidoPaterno(datos.getApellidoPaterno());
        existente.setApellidoMaterno(datos.getApellidoMaterno());
        existente.setFechaNacimiento(datos.getFechaNacimiento());
        existente.setCorreoElectronico(datos.getCorreoElectronico());
        existente.setTelefono(datos.getTelefono());
        existente.setDireccionCompleta(datos.getDireccionCompleta());
        existente.setComuna(datos.getComuna());

        if (datos.getPerfilVoluntario() != null) {
            if (existente.getPerfilVoluntario() == null) {
                datos.getPerfilVoluntario().setVoluntario(existente);
                existente.setPerfilVoluntario(datos.getPerfilVoluntario());
            } else {
                var p = existente.getPerfilVoluntario();
                var d = datos.getPerfilVoluntario();
                p.setDiasDisponibles(d.getDiasDisponibles());
                p.setHorarioDisponible(d.getHorarioDisponible());
                p.setTipoVivienda(d.getTipoVivienda());
                p.setRegion(d.getRegion());
                p.setUrlCv(d.getUrlCv());
                p.setUrlCertificadoAntecedentes(d.getUrlCertificadoAntecedentes());
            }
        }
        return voluntarioRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!voluntarioRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Voluntario no encontrado");
        }
        voluntarioRepository.deleteById(id);
    }
}
