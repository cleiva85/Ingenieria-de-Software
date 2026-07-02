package com.abrazame.service_gestion.dto;
import lombok.Data;

/** DTO para que el Voluntario/Admin confirme la entrega física al beneficiario. */
@Data
public class ConfirmarEntregaRequest {
    private String confirmadoPor;   // nombre + email del voluntario/admin
    private String observaciones;
}
