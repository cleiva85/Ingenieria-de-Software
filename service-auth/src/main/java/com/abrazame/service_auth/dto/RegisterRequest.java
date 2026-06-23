package com.abrazame.service_auth.dto;

import com.abrazame.service_auth.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    public String nombre;

    public String nombre2;

    public String apellidoP;

    public String apellidoM;

    public String rut;
    public String fechaNacimiento;
    public String telefono;

    @NotBlank
    @Email
    public String email;

    // Opcional — voluntarios no tienen contraseña al registrarse
    public String password;

    @NotNull
    public Usuario.Rol rol;
}
