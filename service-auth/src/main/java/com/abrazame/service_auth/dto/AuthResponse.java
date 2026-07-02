package com.abrazame.service_auth.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private boolean success;
    private String mensaje;
    private String rol;
    private String nombre;
    private String redirectUrl;

    public AuthResponse(boolean success, String mensaje) {
        this.success = success;
        this.mensaje = mensaje;
    }

    public AuthResponse(boolean success, String mensaje, String rol, String nombre, String redirectUrl) {
        this.success = success;
        this.mensaje = mensaje;
        this.rol = rol;
        this.nombre = nombre;
        this.redirectUrl = redirectUrl;
    }
}
