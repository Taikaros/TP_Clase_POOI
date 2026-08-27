package ar.edu.unam.veterinaria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "servicios")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "turno_id", nullable = false)
    protected Turno turno;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tipo_servicio_id", nullable = false)
    protected TipoServicio tipoServicio;

    @Column(nullable = false)
    protected Double precioHistorico;

    public Servicio() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Turno getTurno() { return turno; }
    public void setTurno(Turno turno) {
        if (turno == null) throw new IllegalArgumentException("El servicio debe pertenecer a un turno.");
        this.turno = turno;
    }

    public TipoServicio getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(TipoServicio tipoServicio) {
        if (tipoServicio == null) throw new IllegalArgumentException("El tipo de servicio es obligatorio.");
        this.tipoServicio = tipoServicio;
        // Congelamos el precio base en el precio histórico al asignar el tipo
        this.precioHistorico = tipoServicio.getPrecioBase();
    }

    public Double getPrecioHistorico() { return precioHistorico; }
    public void setPrecioHistorico(Double precioHistorico) {
        if (precioHistorico == null || precioHistorico < 0) throw new IllegalArgumentException("El precio histórico no puede ser negativo.");
        this.precioHistorico = precioHistorico;
    }

    public Double calcularCosto() {
        return this.precioHistorico != null ? this.precioHistorico : 0.0;
    }

    public abstract String getDetallesServicio();
}