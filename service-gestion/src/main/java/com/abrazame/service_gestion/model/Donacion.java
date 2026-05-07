package com.abrazame.service_gestion.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "donaciones")
@Data
public class Donacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String donId;           // codigo DON-001, DON-002...
    private Long donanteId;
    private String nombreDonante;   // desnormalizado para mostrar en panel
    private String tipoDonante;     // "Persona Natural" | "Empresa"
    private String articuloNombre;
    private Long articuloId;
    private Integer cantidad;
    private String estado;          // "Pendiente" | "Aprobado" | "Rechazado"
    private LocalDate fechaEntrega;
    private String puntoEntrega;    // "Sede Providencia" | "Casillero Mall Plaza Tobalaba"
    private String horaEstimada;
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
        if (estado == null) estado = "Pendiente";
    }

    @Transient
    private Object datosDonante;
}
