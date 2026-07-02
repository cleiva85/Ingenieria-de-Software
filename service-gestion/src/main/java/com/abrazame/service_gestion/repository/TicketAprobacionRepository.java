package com.abrazame.service_gestion.repository;
import com.abrazame.service_gestion.model.TicketAprobacionDirector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketAprobacionRepository extends JpaRepository<TicketAprobacionDirector, Long> {
    List<TicketAprobacionDirector> findByDonacionId(Long donacionId);
    Optional<TicketAprobacionDirector> findByDonacionIdAndEstado(Long donacionId, TicketAprobacionDirector.EstadoTicket estado);
    List<TicketAprobacionDirector> findByEstado(TicketAprobacionDirector.EstadoTicket estado);
}
