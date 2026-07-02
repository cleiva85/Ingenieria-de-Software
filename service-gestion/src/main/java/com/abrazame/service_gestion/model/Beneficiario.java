package com.abrazame.service_gestion.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Beneficiario — niño, familia o grupo hogar que recibe la donación.
 * Pertenece a una Fundación/Residencia colaboradora (fundacionId).
 * El contacto es SIEMPRE un funcionario de esa institución,
 * nunca un familiar externo.
 * v3.0 — Fundación Abrázame.
 */
@Entity
@Table(name = "beneficiarios")
@Data
public class Beneficiario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_beneficiario", nullable = false)
    private TipoBeneficiario tipoBeneficiario = TipoBeneficiario.Niño;

    private LocalDate fechaNacimiento;
    private String rangoEdad;       // 0-2 años, 3-6 años, 7-12 años, 13-17 años

    @Enumerated(EnumType.STRING)
    private Genero genero = Genero.No_especificado;

    @Column(columnDefinition = "TEXT")
    private String descripcionNecesidad;

    /** FK a la fundación/residencia donde está el beneficiario */
    @Column(nullable = false)
    private Long fundacionId;

    /** Datos cargados desde fundaciones_colaboradoras al consultar (no persistidos) */
    @Transient
    private String fundacionResidencia;
    @Transient
    private String region;

    /** Contacto = funcionario de ESA fundación/residencia, nunca un familiar */
    @Column(nullable = false, length = 200)
    private String contactoNombre;

    @Column(nullable = false, length = 100)
    private String contactoCargo;

    private String contactoTelefono;
    private String contactoEmail;

    @Column(nullable = false)
    private Boolean activo = true;

    private LocalDate fechaIngreso;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) fechaRegistro = LocalDateTime.now();
    }

    /** Calcula la edad en años o retorna null para Familia/Grupo Hogar sin fecha de nacimiento */
    @Transient
    public Integer getEdadActual() {
        if (fechaNacimiento == null) return null;
        return java.time.Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    public enum TipoBeneficiario { Niño, Familia, Grupo_Hogar }
    public enum Genero { Masculino, Femenino, No_especificado }
}
