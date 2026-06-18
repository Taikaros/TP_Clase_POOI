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
    public Mascota(String nombreMascota, String especie, String raza, LocalDate fechaNacimiento, Cliente dueno, Long numeroFicha) {
        if(dueno == null) {
            throw new IllegalArgumentException("La mascota debe tener un dueño asignado.");
        }
        this.nombreMascota = nombreMascota;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.dueno = dueno;
        this.numeroFicha = numeroFicha;
    }

    // getters y setters

    public String getDetallesMascota() {
        return "Nombre: " + this.nombreMascota + ", Especie: " + this.especie + ", Raza: " + this.raza +
                ", Fecha de Nacimiento: " + this.fechaNacimiento +", Ficha: " + this.numeroFicha ;
    }

    public Cliente getDueno() {
        return dueno;
    }

    public void setDatosMascota(String nombreMascota, String especie, String raza, LocalDate fechaNacimiento, Long numeroFicha) {
        this.nombreMascota = nombreMascota;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.numeroFicha = numeroFicha;
    }
    public void setDueno(Cliente dueno) {
        this.dueno = dueno;
    }
    
}
