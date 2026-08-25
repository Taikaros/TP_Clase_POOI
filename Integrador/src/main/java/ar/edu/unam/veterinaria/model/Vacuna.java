package ar.edu.unam.veterinaria.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vacunas_catalogo")
public class Vacuna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombreComercial;
    private String enfermedad;
    private Integer periodicidad; // Meses

    public Vacuna() {}

    // Constructor original
    public Vacuna(String nombreComercial, String enfermedad) {
        this.nombreComercial = nombreComercial;
        this.enfermedad = enfermedad;
        this.periodicidad = 12; // Valor por defecto
    }

    // Nuevo Constructor para el Controlador
    public Vacuna(String nombreComercial, String enfermedad, Integer periodicidad) {
        this.nombreComercial = nombreComercial;
        this.enfermedad = enfermedad;
        this.periodicidad = periodicidad;
    }

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }

    public String getEnfermedad() { return enfermedad; }
    public void setEnfermedad(String enfermedad) { this.enfermedad = enfermedad; }

    public Integer getPeriodicidad() { return periodicidad; }
    public void setPeriodicidad(Integer periodicidad) { this.periodicidad = periodicidad; }

    // Métodos de dominio originales que ya tenías
    public String getDetallesVacuna() {
        return this.nombreComercial + " (" + this.enfermedad + ")";
    }

    public LocalDate calcularProximaAplicacion(LocalDate fechaUltima) {
        if (this.periodicidad == null) return fechaUltima.plusMonths(12);
        return fechaUltima.plusMonths(this.periodicidad);
    }
}