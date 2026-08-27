package ar.edu.unam.veterinaria.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vacunas_catalogo")
public class Vacuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreComercial;

    @Column(nullable = false)
    private String enfermedad;

    @Column(nullable = false)
    private Integer periodicidad;

    public Vacuna() {}

    public Vacuna(String nombreComercial, String enfermedad, Integer periodicidad) {
        setNombreComercial(nombreComercial);
        setEnfermedad(enfermedad);
        setPeriodicidad(periodicidad);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) {
        if (nombreComercial == null || nombreComercial.trim().isEmpty()) throw new IllegalArgumentException("El nombre de la vacuna es obligatorio.");
        this.nombreComercial = nombreComercial;
    }

    public String getEnfermedad() { return enfermedad; }
    public void setEnfermedad(String enfermedad) {
        if (enfermedad == null || enfermedad.trim().isEmpty()) throw new IllegalArgumentException("La enfermedad que previene es obligatoria.");
        this.enfermedad = enfermedad;
    }

    public Integer getPeriodicidad() { return periodicidad; }
    public void setPeriodicidad(Integer periodicidad) {
        if (periodicidad == null || periodicidad <= 0) throw new IllegalArgumentException("La periodicidad debe ser de al menos 1 mes.");
        this.periodicidad = periodicidad;
    }

    public LocalDate calcularProximaAplicacion(LocalDate fechaUltima) {
        if (fechaUltima == null) return null;
        return fechaUltima.plusMonths(this.periodicidad);
    }
}