package com.abrazame.service_gestion.dto;
import lombok.Data;

/** DTO para que el Director apruebe o rechace un ticket de entrega. */
@Data
public class ResolverTicketRequest {
    private String estado;        // "Aprobado" | "Rechazado"
    private String motivo;
    private String directorNombre;
}
