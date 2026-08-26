package ar.edu.unam.veterinaria.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mascotas")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreMascota;

    @Column(nullable = false)
    private String especie;

    @Column(nullable = false)
    private String raza;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente dueno;

    private Long numeroFicha;

    @OneToMany(mappedBy = "mascota")
    private List<Turno> historialTurnos = new ArrayList<>();

    public Mascota() {}

    public Mascota(String nombreMascota, String especie, String raza, LocalDate fechaNacimiento, Cliente dueno, Long numeroFicha) {
        setNombreMascota(nombreMascota);
        setEspecie(especie);
        setRaza(raza);
        setFechaNacimiento(fechaNacimiento);
        setDueno(dueno);
        setNumeroFicha(numeroFicha);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) {
        if (nombreMascota == null || nombreMascota.trim().isEmpty()) throw new IllegalArgumentException("El nombre de la mascota es obligatorio.");
        this.nombreMascota = nombreMascota;
    }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) {
        if (especie == null || especie.trim().isEmpty()) throw new IllegalArgumentException("La especie es obligatoria.");
        this.especie = especie;
    }

    public String getRaza() { return raza; }
    public void setRaza(String raza) {
        if (raza == null || raza.trim().isEmpty()) throw new IllegalArgumentException("La raza es obligatoria.");
        this.raza = raza;
    }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
        if (fechaNacimiento.isAfter(LocalDate.now())) throw new IllegalArgumentException("La fecha de nacimiento no puede ser en el futuro.");
        this.fechaNacimiento = fechaNacimiento;
    }

    public Cliente getDueno() { return dueno; }
    public void setDueno(Cliente dueno) {
        if (dueno == null) throw new IllegalArgumentException("La mascota debe tener un dueño asignado obligatoriamente.");
        this.dueno = dueno;
    }

    public Long getNumeroFicha() { return numeroFicha; }
    public void setNumeroFicha(Long numeroFicha) {
        if (numeroFicha != null && numeroFicha < 0) throw new IllegalArgumentException("El número de ficha no puede ser negativo.");
        this.numeroFicha = numeroFicha;
    }

    public List<Turno> getHistorialTurnos() { return historialTurnos; }

    public int obtenerEdad() {
        return Period.between(this.fechaNacimiento, LocalDate.now()).getYears();
    }

    public boolean tieneVacunaVigente(Vacuna vacunaAControlar, LocalDate fechaTurnoDeseado) {
        if (vacunaAControlar == null || fechaTurnoDeseado == null) return false;
        for (Turno turno : this.historialTurnos) {
            if (turno.getEstado() != EstadoTurno.CANCELADO && !turno.getFecha().isAfter(fechaTurnoDeseado)) {
                for (Servicio servicio : turno.getServicios()) {
                    if (servicio instanceof Vacunacion) {
                        Vacunacion vacAnterior = (Vacunacion) servicio;
                        if (vacAnterior.getVacunaAplicada() != null && vacAnterior.getVacunaAplicada().getId().equals(vacunaAControlar.getId())) {
                            LocalDate fechaVencimiento = vacAnterior.getVacunaAplicada().calcularProximaAplicacion(turno.getFecha());
                            if (fechaVencimiento.isAfter(fechaTurnoDeseado) || fechaVencimiento.isEqual(fechaTurnoDeseado)) {
                                return true; 
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}