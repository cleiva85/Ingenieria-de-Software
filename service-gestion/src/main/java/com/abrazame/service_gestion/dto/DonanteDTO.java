package com.abrazame.service_gestion.dto;

import lombok.Data;

/**
 * Projection del Donante que llega desde service-donante.
 * Solo trae los campos necesarios para mostrar en gestión.
 */
@Data
public class DonanteDTO {
    private Long id;
    private String rut;
    private String primerNombre;
    private String apellidoPaterno;
    private String correoElectronico;  // Coincide con el campo real en Donante.java
    private String telefono;
    private String comuna;
}
