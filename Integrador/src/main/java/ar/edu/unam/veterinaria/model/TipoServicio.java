package ar.edu.unam.veterinaria.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tipos_servicio")
public class TipoServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombreDescriptivo;
    private Double precioBase;
    private double duracion;
    private Integer limiteCupoDiario;

    public TipoServicio() {}

    public TipoServicio(String nombreDescriptivo, Double precioBase, double duracion, Integer limiteCupoDiario) {
        this.nombreDescriptivo = nombreDescriptivo;
        this.precioBase = precioBase;
        this.duracion = duracion;
        this.limiteCupoDiario = limiteCupoDiario;
    }

    public void setDetalles(String nombreDescriptivo, Double precioBase, double duracion, Integer limiteCupoDiario) {
        this.nombreDescriptivo = nombreDescriptivo;
        this.precioBase = precioBase;
        this.duracion = duracion;
        this.limiteCupoDiario = limiteCupoDiario;
    }

    public Long getId() { return id; }
    public String getNombreDescriptivo() { return nombreDescriptivo; }
    public Double getPrecioBase() { return precioBase; }
    public double getDuracion() { return duracion; }
    public Integer getLimiteCupoDiario() { return limiteCupoDiario; }
}