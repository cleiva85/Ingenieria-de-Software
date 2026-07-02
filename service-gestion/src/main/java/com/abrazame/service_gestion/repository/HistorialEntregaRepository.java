package com.abrazame.service_gestion.repository;
import com.abrazame.service_gestion.model.HistorialEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialEntregaRepository extends JpaRepository<HistorialEntrega, Long> {
    List<HistorialEntrega> findByBeneficiarioId(Long beneficiarioId);
    List<HistorialEntrega> findByArticuloId(Long articuloId);
    List<HistorialEntrega> findAllByOrderByFechaEntregaDesc();
}
