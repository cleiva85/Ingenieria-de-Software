package com.abrazame.service_auth.repository;

import com.abrazame.service_auth.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByTokenActivacion(String token);
    boolean existsByEmail(String email);
    boolean existsByRut(String rut);
    java.util.Optional<Usuario> findByRut(String rut);
    List<Usuario> findByRolAndActivo(Usuario.Rol rol, boolean activo);
}
