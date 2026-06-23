package com.abrazame.service.donante.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "donantes_persona_natural")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonantePersonaNatural {

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

    private String correoElectronico;
    private String telefono;
    private String comuna;
    private String direccionCompleta;

    private Integer cantidad;
    private String estadoDonacion;
    private String articuloNombre;  // que van a donar
    private Long articuloId;         // id en catalogo  // "Nuevo" | "Usado en buen estado"

    private String urlImagen;
    private String urlVideo;
}
