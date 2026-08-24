package ar.edu.unam.veterinaria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "servicio_peluqueria")
public class Peluqueria extends Servicio {
    private String tipoCorte;
    private String observaciones;

    public Peluqueria() {}

    public String getDetallesPeluqueria() {
        return "Peluquería: " + (tipoCorte != null ? tipoCorte : "Estética general");
    }

    @Override
    public String getDetallesServicio() {
        return getDetallesPeluqueria();
    }

    public String getTipoCorte() { return tipoCorte; }
    public void setTipoCorte(String tipoCorte) { this.tipoCorte = tipoCorte; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}