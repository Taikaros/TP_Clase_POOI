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

    // ---> REGLA DE NEGOCIO: CONTROL DE CUPOS <---
    public void validarCupo(java.time.LocalDate fecha, int ocupacionActual) throws ar.edu.unam.veterinaria.exception.CupoLLeno {
        if (this.limiteCupoDiario != null && this.limiteCupoDiario > 0) {
            if (ocupacionActual >= this.limiteCupoDiario) {
                throw new ar.edu.unam.veterinaria.exception.CupoLLeno(
                    "El servicio '" + this.nombreDescriptivo + "' alcanzó su cupo máximo de " + 
                    this.limiteCupoDiario + " reservas para el día " + fecha.toString() + "."
                );
            }
        }
    }
    public void setDetalles(String nombreDescriptivo, Double precioBase, double duracion, Integer limiteCupoDiario) {
        if (precioBase != null && precioBase < 0) {
            throw new IllegalArgumentException("Regla de Integridad: El precio base no puede ser negativo.");
        }
        if (limiteCupoDiario != null && limiteCupoDiario < 0) {
            throw new IllegalArgumentException("Regla de Integridad: El cupo diario no puede ser menor a cero.");
        }
        this.nombreDescriptivo = nombreDescriptivo;
        this.precioBase = precioBase;
        this.duracion = duracion;
        this.limiteCupoDiario = limiteCupoDiario;
    }
}