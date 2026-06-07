package ar.edu.unam.veterinaria.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

//@MappedSuperclass le indica a JPA que esta clase no tendra un tabla propia, sino que la heredara sus columnas a las tabalas hijas(cliente y veterinario)
@MappedSuperclass
public abstract class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @Column(nullable = false)
    protected String nombre;
    @Column(nullable = false)
    protected String apellido;
    protected String telefono;
    protected String email;
    protected Persona() {
    }
    public Persona(String nombre, String apellido, String telefono, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
    }
    // Getters y Setters
    public Long getId() {
        return this.id;
    }
    public String getNombre() {
        return this.nombre;
    }
    public String getApellido() {
        return this.apellido;
    }
    public String getContacto() {
        return "Teléfono: " + this.telefono + ", Email: " + this.email;
    }
    public void setContacto(String telefono, String email) {
        this.telefono = telefono;
        this.email = email;
    }
    public void setDatosPersonales(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
    }
}
