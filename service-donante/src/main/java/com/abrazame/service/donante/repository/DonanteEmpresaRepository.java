package com.abrazame.service.donante.repository;

import com.abrazame.service.donante.model.DonanteEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DonanteEmpresaRepository extends JpaRepository<DonanteEmpresa, Long> {
    Optional<DonanteEmpresa> findByRut(String rut);
}
