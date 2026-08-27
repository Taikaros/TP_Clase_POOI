package ar.edu.unam.veterinaria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "servicio_vacunacion")
public class Vacunacion extends Servicio {

    @ManyToOne(optional = false)
    @JoinColumn(name = "vacuna_id", nullable = false)
    private Vacuna vacunaAplicada;

    public Vacunacion() {}

    public Vacuna getVacunaAplicada() { return vacunaAplicada; }
    public void setVacunaAplicada(Vacuna vacunaAplicada) {
        if (vacunaAplicada == null) throw new IllegalArgumentException("Debe especificar qué vacuna se aplicó.");
        this.vacunaAplicada = vacunaAplicada;
    }

    @Override
    public String getDetallesServicio() {
        return "Vacunación: " + (vacunaAplicada != null ? vacunaAplicada.getNombreComercial() : "Desconocida");
    }
}