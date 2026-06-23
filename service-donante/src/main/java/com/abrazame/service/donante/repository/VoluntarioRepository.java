package com.abrazame.service.donante.repository;

import com.abrazame.service.donante.model.Voluntario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoluntarioRepository extends JpaRepository<Voluntario, Long> {
    Optional<Voluntario> findByRut(String rut);
    Optional<Voluntario> findByCorreoElectronico(String correo);
}
