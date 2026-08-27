package ar.edu.unam.veterinaria.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
public class Cliente extends Persona {

    @Column(unique = true, nullable = false)
    private String dni;

    @OneToMany(mappedBy = "dueno", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Mascota> mascotas = new ArrayList<>();

    public Cliente() {}

    public Cliente(String nombre, String apellido, String telefono, String email, String dni) {
        super(nombre, apellido, telefono, email);
        setDni(dni);
    }

    public String getDni() { return dni; }
    
    public void setDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) throw new IllegalArgumentException("El DNI es obligatorio.");
        this.dni = dni;
    }

    public List<Mascota> getMascotas() { return mascotas; }

    public void registrarMascota(Mascota mascota) {
        if (mascota == null) throw new IllegalArgumentException("No se puede registrar una mascota nula.");
        if (!this.mascotas.contains(mascota)) {
            this.mascotas.add(mascota);
            mascota.setDueno(this);
        }
    }

    public Mascota obtenerMascotaPorFicha(Long numeroFicha) throws ar.edu.unam.veterinaria.exception.MascotaNoEncontrada {
        if (this.mascotas == null || this.mascotas.isEmpty()) throw new ar.edu.unam.veterinaria.exception.MascotaNoEncontrada("El cliente no tiene mascotas.");
        for (Mascota m : this.mascotas) {
            if (m.getNumeroFicha() != null && m.getNumeroFicha().equals(numeroFicha)) return m;
        }
        throw new ar.edu.unam.veterinaria.exception.MascotaNoEncontrada("No se encontró mascota con ficha FCH-" + numeroFicha);
    }
}