package com.abrazame.service_gestion.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Registro inmutable de cada entrega confirmada a un beneficiario.
 * Permite ver de dónde salió el artículo (qué donación) y a dónde
 * fue (qué beneficiario, en qué fundación/residencia).
 * v3.0 — Fundación Abrázame.
 */
@Entity
@Table(name = "historial_entregas")
@Data
public class HistorialEntrega {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long donacionId;
    private String donacionCodigo;     // ej: DON-001
    private String articuloNombre;
    private Long articuloId;           // referencia al catálogo, para el descuento de stock
    private Integer cantidad;

    private Long beneficiarioId;
    private String beneficiarioNombre;
    private String fundacionResidencia; // snapshot al momento de la entrega
    private String region;

    private String confirmadoPor;       // nombre + rol del voluntario/admin que confirmó
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime fechaEntrega;

    @PrePersist
    public void prePersist() {
        if (fechaEntrega == null) fechaEntrega = LocalDateTime.now();
    }
}
