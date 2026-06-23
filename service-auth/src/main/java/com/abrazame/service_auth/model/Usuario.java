package com.abrazame.service_auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "nombre2")
    private String nombre2;

    @NotBlank
    @Column(name = "apellido_p", nullable = false)
    private String apellidoP;

    @NotBlank
    @Column(name = "apellido_m", nullable = false)
    private String apellidoM;

    @Column(name = "rut", unique = true)
    private String rut;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "telefono")
    private String telefono;

    @NotBlank
    @Email
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;

    @Column(name = "activo")
    private boolean activo = false;

    @Column(name = "intentos_fallidos")
    private int intentosFallidos = 0;

    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    // Token único para activación/creación de contraseña
    @Column(name = "token_activacion", unique = true)
    private String tokenActivacion;

    @Column(name = "token_expira")
    private LocalDateTime tokenExpira;

    public enum Rol {
        Donante, Voluntario, Admin, Director, SuperAdmin
    }
}
