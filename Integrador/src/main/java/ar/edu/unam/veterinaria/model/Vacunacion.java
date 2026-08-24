package ar.edu.unam.veterinaria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "servicio_vacunacion")
public class Vacunacion extends Servicio {
    
    @ManyToOne
    @JoinColumn(name = "vacuna_id")
    private Vacuna vacunaAplicada;
    
    private LocalDate fechaAplicacion;

    public Vacunacion() {}

    public String getDetallesVacunacion() {
        return "Vacunación: " + (vacunaAplicada != null ? vacunaAplicada.getNombreComercial() : "A definir");
    }

    @Override
    public String getDetallesServicio() {
        return getDetallesVacunacion();
    }

    public void setFechaAplicacion(LocalDate fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public Vacuna getVacunaAplicada() { return vacunaAplicada; }
    public void setVacunaAplicada(Vacuna vacunaAplicada) { this.vacunaAplicada = vacunaAplicada; }
    public LocalDate getFechaAplicacion() { return fechaAplicacion; }
}