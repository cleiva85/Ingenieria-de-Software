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

    private String donId;
    private Long donanteId;
    private String nombreDonante;
    private String tipoDonante;
    private String articuloNombre;
    private Long articuloId;
    private Integer cantidad;
    private String estado;
    private LocalDate fechaEntrega;
    private String puntoEntrega;
    private String horaEstimada;
    private LocalDateTime fechaCreacion;
    private String urlImagen;
    private String urlVideo;
    private String comentarioVoluntario;
    private String emailDonante;
    private Boolean recibida = false;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
        if (estado == null) estado = "Pendiente";
    }

    @Transient
    private Object datosDonante;
}
