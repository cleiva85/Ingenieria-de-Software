package com.abrazame.service.donante.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "perfil_voluntario")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerfilVoluntario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String diasDisponibles;      // "lunes, miercoles, sabado"
    private String horarioDisponible;    // "manana" | "tarde" | "manana, tarde"
    private String tipoVivienda;         // "Casa" | "Departamento" | "Otro"
    private String region;
    private String urlCv;
    private String urlCertificadoAntecedentes;

    @OneToOne
    @JoinColumn(name = "voluntario_id", unique = true)
    @JsonBackReference
    private Voluntario voluntario;
}
