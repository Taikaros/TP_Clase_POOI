package ar.edu.unam.veterinaria.model;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false)
    protected String nombre;

    @Column(nullable = false)
    protected String apellido;

    @Column(nullable = false)
    protected String telefono;

    protected String email;

    protected boolean activo = true;

    public Persona() {}

    public Persona(String nombre, String apellido, String telefono, String email) {
        setNombre(nombre);
        setApellido(apellido);
        setTelefono(telefono);
        setEmail(email);
    }

    // Getters
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public boolean isActivo() { return activo; }

    // Setters Defensivos
    public void setId(Long id) { this.id = id; }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) throw new IllegalArgumentException("El nombre es obligatorio.");
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) throw new IllegalArgumentException("El apellido es obligatorio.");
        this.apellido = apellido;
    }

    public void setTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) throw new IllegalArgumentException("El teléfono es obligatorio.");
        this.telefono = telefono;
    }

    public void setEmail(String email) {
        this.email = email; // El email puede ser opcional en algunos casos
    }

    public void setActivo(boolean activo) { this.activo = activo; }

    // Métodos utilitarios legacy
    public void setDatosPersonales(String nombre, String apellido) {
        setNombre(nombre);
        setApellido(apellido);
    }

    public void setContacto(String telefono, String email) {
        setTelefono(telefono);
        setEmail(email);
    }
    
    public String getContacto() {
        return "Teléfono: " + this.telefono + ", Email: " + (this.email != null ? this.email : "Sin email");
    }
}