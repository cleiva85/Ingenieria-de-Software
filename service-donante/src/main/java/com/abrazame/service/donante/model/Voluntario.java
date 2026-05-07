package com.abrazame.service.donante.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "voluntarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Voluntario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String rut;

    private String primerNombre;
    private String segundoNombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private LocalDate fechaNacimiento;

    @Column(unique = true)
    private String correoElectronico;
    private String telefono;

    private String direccionCompleta;
    private String comuna;

    @OneToOne(mappedBy = "voluntario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private PerfilVoluntario perfilVoluntario;
}
