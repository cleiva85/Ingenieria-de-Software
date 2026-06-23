package com.abrazame.service_catalogo.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "articulos_catalogo")
@Data @NoArgsConstructor @AllArgsConstructor
public class ArticuloCatalogo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String categoria;
    private String subcategoria;
    private String rangoEdad;
    private String prioridad;
    private Integer metaStock;
    private Integer stockActual;
    private Boolean activo;
    private String descripcion;
}
