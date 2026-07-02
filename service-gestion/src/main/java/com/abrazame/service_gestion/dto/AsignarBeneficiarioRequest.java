package com.abrazame.service_gestion.dto;
import lombok.Data;

/** DTO para que el Director asigne un beneficiario a una donación y cree el ticket. */
@Data
public class AsignarBeneficiarioRequest {
    private Long beneficiarioId;
    private Long directorId;
    private String directorNombre;
    private String observaciones;
}
