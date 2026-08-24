package ar.edu.unam.veterinaria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "servicios")
@Inheritance(strategy = InheritanceType.JOINED) // Estrategia clave para herencia SQL
public abstract class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_id")
    protected Turno turno;

    @ManyToOne
    @JoinColumn(name = "tipo_servicio_id")
    protected TipoServicio tipoServicio;

    protected Double precioHistorico;

    public Servicio() {}

    public abstract String getDetallesServicio();

    public Double calcularCosto() {
        return this.precioHistorico != null ? this.precioHistorico : 0.0;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Turno getTurno() { return turno; }
    public void setTurno(Turno turno) { this.turno = turno; }
    public TipoServicio getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(TipoServicio tipoServicio) { 
        this.tipoServicio = tipoServicio; 
        if (tipoServicio != null) {
            this.precioHistorico = tipoServicio.getPrecioBase(); // Guardamos el precio al momento de instanciar
        }
    }
    public Double getPrecioHistorico() { return precioHistorico; }
}