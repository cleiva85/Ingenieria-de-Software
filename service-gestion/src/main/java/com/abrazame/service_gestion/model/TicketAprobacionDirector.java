package com.abrazame.service_gestion.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Ticket de Aprobación — generado por el Director al asignar
 * un beneficiario a una donación aprobada.
 * Permite al Voluntario/Admin confirmar la entrega física.
 * v2.0 — Fundación Abrázame.
 */
@Entity
@Table(name = "ticket_aprobacion_director")
@Data
public class TicketAprobacionDirector {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long donacionId;

    @Column(nullable = false)
    private Long beneficiarioId;

    private Long directorUsuarioId;
    private String directorNombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTicket estado = EstadoTicket.Pendiente;

    private LocalDateTime fechaDecision;

    @Column(columnDefinition = "TEXT")
    private String motivoRechazo;

    @Column(columnDefinition = "TEXT")
    private String observacionesDirector;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
    }

    public enum EstadoTicket { Pendiente, Aprobado, Rechazado }
}
