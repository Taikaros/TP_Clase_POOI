package ar.edu.unam.veterinaria.model;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class Cliente extends Persona {
    
    @OneToMany(mappedBy = "dueno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mascota> mascotas = new ArrayList<>();

    public Cliente() {
        super();
    }
    public Cliente(String nombre, String apellido, String telefono, String email) {
        super(nombre, apellido, telefono, email);
    }
    public List<Mascota> getMascotas() {
        return mascotas;
    }

    public void registrarMascota(Mascota mascota) {
        mascotas.add(mascota);
        mascota.setDueno(this);
    }
}
