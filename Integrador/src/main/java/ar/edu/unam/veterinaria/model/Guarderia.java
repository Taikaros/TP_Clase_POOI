package ar.edu.unam.veterinaria.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "servicio_guarderia")
public class Guarderia extends Servicio {

    @Column(nullable = false)
    private String jaulaAsignada;

    private String alimentacionEspecifica;
    private boolean requiereActividad;
    private String observaciones;
    private LocalDate fechaSalida;

    public Guarderia() {}

    public void registrarReserva(Mascota mascota, LocalDate fechaIngreso, String jaulaDeseada, List<String> jaulasOcupadas) throws ar.edu.unam.veterinaria.exception.JaulaNoDisponible {
        if (jaulaDeseada == null || jaulaDeseada.trim().isEmpty()) throw new IllegalArgumentException("La jaula deseada es obligatoria.");
        if (jaulasOcupadas != null && jaulasOcupadas.contains(jaulaDeseada)) {
            throw new ar.edu.unam.veterinaria.exception.JaulaNoDisponible("La jaula '" + jaulaDeseada + "' ya se encuentra ocupada.");
        }
        this.jaulaAsignada = jaulaDeseada;
    }

    public String getJaulaAsignada() { return jaulaAsignada; }
    public void setJaulaAsignada(String jaulaAsignada) {
        if (jaulaAsignada == null || jaulaAsignada.trim().isEmpty()) throw new IllegalArgumentException("La jaula asignada es obligatoria.");
        this.jaulaAsignada = jaulaAsignada;
    }

    public String getAlimentacionEspecifica() { return alimentacionEspecifica; }
    public void setAlimentacionEspecifica(String alimentacionEspecifica) {
        this.alimentacionEspecifica = alimentacionEspecifica != null ? alimentacionEspecifica.trim() : "";
    }

    public boolean isRequiereActividad() { return requiereActividad; }
    public void setRequiereActividad(boolean requiereActividad) { this.requiereActividad = requiereActividad; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones != null ? observaciones.trim() : "";
    }

    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) {
        if (fechaSalida == null) throw new IllegalArgumentException("La fecha de salida es obligatoria.");
        this.fechaSalida = fechaSalida;
    }

    @Override
    public String getDetallesServicio() {
        return "Guardería (Jaula: " + this.jaulaAsignada + ")";
    }
}