package ar.edu.unam.veterinaria.model;
import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Mascota {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombreMascota;
    private String especie;
    private String raza;
    private LocalDate fechaNacimiento;
    private Long numeroFicha;
    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente dueno;

    public Mascota() {
    }
    public Mascota(Long id,String nombreMascota, String especie, String raza, LocalDate fechaNacimiento, Cliente dueno, Long numeroFicha) {
        if(dueno == null) {
            throw new IllegalArgumentException("La mascota debe tener un dueño asignado.");
        }
        this.id = id;
        this.nombreMascota = nombreMascota;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.dueno = dueno;
        this.numeroFicha = numeroFicha;
    }

    // getters y setters
    public Long getId() {
        return id;
    }
    public String getNombreMascota() {
        return nombreMascota;
    }
    public String getEspecie() {
        return especie;
    }
    public String getRaza() {
        return raza;
    }
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    public Long getNumeroFicha() {
        return numeroFicha;
    }
    public String getDetallesMascota() {
        return "Nombre: " + this.nombreMascota + ", Especie: " + this.especie + ", Raza: " + this.raza +
                ", Fecha de Nacimiento: " + this.fechaNacimiento +", Ficha: " + this.numeroFicha ;
    }
    public Cliente getDueno() {
        return dueno;
    }
    public void setId(Long id) {
        this.id = id;

    }
    public void setDueno(Cliente dueno) {
        this.dueno = dueno;
    }
    
}
