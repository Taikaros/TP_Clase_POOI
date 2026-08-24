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
    private int periodicidad; // en meses

    public Vacuna() {}

    public String getDetallesVacuna() {
        return nombreComercial + " (" + enfermedad + ")";
    }

    public LocalDate calcularProximaAplicacion(LocalDate fechaUltima) {
        return fechaUltima.plusMonths(periodicidad);
    }

    // Getters y Setters
    public Long getId() { return id; }
    public String getNombreComercial() { return nombreComercial; }
    public String getEnfermedad() { return enfermedad; }
    public int getPeriodicidad() { return periodicidad; }
}