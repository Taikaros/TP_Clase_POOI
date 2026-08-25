package ar.edu.unam.veterinaria.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cliente extends Persona {
    
    @Column(unique = true)
    private String dni;

    @OneToMany(mappedBy = "dueno", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Mascota> mascotas = new ArrayList<>();

    public Cliente() {
        super();
    }

    public Cliente(String nombre, String apellido, String dni, String telefono, String email) {
        super(nombre, apellido, telefono, email);
        this.dni = dni;
    }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public List<Mascota> getMascotas() { return mascotas; }

    public void registrarMascota(Mascota mascota) {
        mascotas.add(mascota);
        mascota.setDueno(this);
    }
}