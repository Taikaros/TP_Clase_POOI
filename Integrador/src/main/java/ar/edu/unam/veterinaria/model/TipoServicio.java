package ar.edu.unam.veterinaria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipos_servicio")
public class TipoServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombreDescriptivo;
    private Double precioBase;
    private Double duracion;
    private Integer limiteCupoDiario;

    public TipoServicio() {}

    public TipoServicio(String nombreDescriptivo, Double precioBase, Double duracion, Integer limiteCupoDiario) {
        this.nombreDescriptivo = nombreDescriptivo;
        this.precioBase = precioBase;
        this.duracion = duracion;
        this.limiteCupoDiario = limiteCupoDiario;
    }

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreDescriptivo() { return nombreDescriptivo; }
    public void setNombreDescriptivo(String nombreDescriptivo) { this.nombreDescriptivo = nombreDescriptivo; }

    public Double getPrecioBase() { return precioBase; }
    public void setPrecioBase(Double precioBase) { this.precioBase = precioBase; }

    public Double getDuracion() { return duracion; }
    public void setDuracion(Double duracion) { this.duracion = duracion; }

    public Integer getLimiteCupoDiario() { return limiteCupoDiario; }
    public void setLimiteCupoDiario(Integer limiteCupoDiario) { this.limiteCupoDiario = limiteCupoDiario; }
}