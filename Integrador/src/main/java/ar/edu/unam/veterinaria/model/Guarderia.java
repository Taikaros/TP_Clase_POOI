package ar.edu.unam.veterinaria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "servicio_guarderia")
public class Guarderia extends Servicio {
    private String jaulaAsignada;
    private String alimentacionEspecifica;
    private boolean requiereActividad;
    private String observaciones;
    private LocalDate fechaSalida; // NUEVO CAMPO

    public Guarderia() {}

    public String getDetallesGuarderia() {
        return "Guardería (Jaula: " + (jaulaAsignada != null ? jaulaAsignada : "A asignar") + ")";
    }

    @Override
    public String getDetallesServicio() {
        return getDetallesGuarderia();
    }

    public String getJaulaAsignada() { return jaulaAsignada; }
    public void setJaulaAsignada(String jaulaAsignada) { this.jaulaAsignada = jaulaAsignada; }

    public String getAlimentacionEspecifica() { return alimentacionEspecifica; }
    public void setAlimentacionEspecifica(String alimentacionEspecifica) { this.alimentacionEspecifica = alimentacionEspecifica; }

    public boolean isRequiereActividad() { return requiereActividad; }
    public void setRequiereActividad(boolean requiereActividad) { this.requiereActividad = requiereActividad; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }
}