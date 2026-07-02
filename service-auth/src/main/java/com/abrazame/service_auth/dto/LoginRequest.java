package com.abrazame.service_auth.dto;

import com.abrazame.service_auth.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    @Email
    public String email;

    @NotBlank
    public String password;

    @NotNull
    public Usuario.Rol rol;
}
