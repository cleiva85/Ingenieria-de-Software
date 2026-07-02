package com.abrazame.service_gestion.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Fundación o Residencia colaboradora donde están físicamente
 * alojados/asistidos los beneficiarios de las donaciones.
 * v3.0 — Fundación Abrázame.
 */
@Entity
@Table(name = "fundaciones_colaboradoras")
@Data
public class FundacionColaboradora {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoInstitucion tipo;

    @Column(nullable = false, length = 100)
    private String region;

    private String ciudad;
    private String direccion;
    private String telefonoContacto;
    private String emailContacto;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) fechaRegistro = LocalDateTime.now();
    }

    public enum TipoInstitucion { Fundación, Residencia }
}
