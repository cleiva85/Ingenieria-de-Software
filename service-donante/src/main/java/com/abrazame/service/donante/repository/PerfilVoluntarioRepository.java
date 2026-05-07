package com.abrazame.service.donante.repository;

import com.abrazame.service.donante.model.PerfilVoluntario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilVoluntarioRepository extends JpaRepository<PerfilVoluntario, Long> {
    PerfilVoluntario findByVoluntarioId(Long voluntarioId);
}
