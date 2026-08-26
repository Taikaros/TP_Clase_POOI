package ar.edu.unam.veterinaria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipos_servicio")
public class TipoServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreDescriptivo;

    @Column(nullable = false)
    private Double precioBase;

    @Column(nullable = false)
    private double duracion;

    private Integer limiteCupoDiario;

    public TipoServicio() {}

    public TipoServicio(String nombreDescriptivo, Double precioBase, double duracion, Integer limiteCupoDiario) {
        setNombreDescriptivo(nombreDescriptivo);
        setPrecioBase(precioBase);
        setDuracion(duracion);
        setLimiteCupoDiario(limiteCupoDiario);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreDescriptivo() { return nombreDescriptivo; }
    public void setNombreDescriptivo(String nombreDescriptivo) {
        if (nombreDescriptivo == null || nombreDescriptivo.trim().isEmpty()) throw new IllegalArgumentException("El nombre del servicio es obligatorio.");
        this.nombreDescriptivo = nombreDescriptivo;
    }

    public Double getPrecioBase() { return precioBase; }
    public void setPrecioBase(Double precioBase) {
        if (precioBase == null || precioBase < 0) throw new IllegalArgumentException("El precio base no puede ser negativo.");
        this.precioBase = precioBase;
    }

    public double getDuracion() { return duracion; }
    public void setDuracion(double duracion) {
        if (duracion <= 0) throw new IllegalArgumentException("El servicio debe durar más de 0 minutos.");
        this.duracion = duracion;
    }

    public Integer getLimiteCupoDiario() { return limiteCupoDiario; }
    public void setLimiteCupoDiario(Integer limiteCupoDiario) {
        if (limiteCupoDiario != null && limiteCupoDiario < 0) throw new IllegalArgumentException("El cupo diario no puede ser negativo.");
        this.limiteCupoDiario = limiteCupoDiario;
    }

    public void validarCupo(java.time.LocalDate fecha, int ocupacionActual) throws ar.edu.unam.veterinaria.exception.CupoLLeno {
        if (this.limiteCupoDiario != null && this.limiteCupoDiario > 0) {
            if (ocupacionActual >= this.limiteCupoDiario) {
                throw new ar.edu.unam.veterinaria.exception.CupoLLeno("El servicio alcanzó su límite diario para la fecha indicada.");
            }
        }
    }
}