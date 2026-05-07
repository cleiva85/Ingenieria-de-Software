package com.abrazame.service_gestion.repository;
import com.abrazame.service_gestion.model.Donacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonacionRepository extends JpaRepository<Donacion, Long> {
    List<Donacion> findByDonanteId(Long donanteId);
    List<Donacion> findByEstado(String estado);
    Optional<Donacion> findByDonId(String donId);
    long countByEstado(String estado);
}
