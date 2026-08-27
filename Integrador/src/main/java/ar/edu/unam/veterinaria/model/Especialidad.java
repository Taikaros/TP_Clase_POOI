package ar.edu.unam.veterinaria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "especialidades")
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_especialidad", nullable = false)
    private String nombreEspecialidad;

    private String descripcion;

    public Especialidad() {}

    public Especialidad(String nombreEspecialidad, String descripcion) {
        setNombreEspecialidad(nombreEspecialidad);
        setDescripcion(descripcion);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreEspecialidad() { return nombreEspecialidad; }

    public void setNombreEspecialidad(String nombreEspecialidad) {
        if (nombreEspecialidad == null || nombreEspecialidad.trim().isEmpty()) throw new IllegalArgumentException("El nombre de la especialidad es obligatorio.");
        this.nombreEspecialidad = nombreEspecialidad;
    }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion != null ? descripcion.trim() : "";
    }

    public String getDetalles() {
        return "Especialidad: " + this.nombreEspecialidad + " - Descripción: " + this.descripcion;
    }
}