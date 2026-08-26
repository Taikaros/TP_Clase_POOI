package ar.edu.unam.veterinaria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "servicio_peluqueria")
public class Peluqueria extends Servicio {

    @Column(nullable = false)
    private String tipoCorte;

    private String observaciones;

    public Peluqueria() {}

    public String getTipoCorte() { return tipoCorte; }
    public void setTipoCorte(String tipoCorte) {
        if (tipoCorte == null || tipoCorte.trim().isEmpty()) throw new IllegalArgumentException("El tipo de corte o servicio es obligatorio.");
        this.tipoCorte = tipoCorte;
    }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones != null ? observaciones.trim() : "";
    }

    @Override
    public String getDetallesServicio() {
        return "Peluquería: " + this.tipoCorte;
    }
}