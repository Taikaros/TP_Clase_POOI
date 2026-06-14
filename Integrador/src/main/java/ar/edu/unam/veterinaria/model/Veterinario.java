package ar.edu.unam.veterinaria.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "veterinarios")
public class Veterinario extends Persona {

    @Column(nullable = false, unique = true)
    private String matricula;

    // multiplicidad de 1 Veterinario tiene Muchas (*) Especialidades
    // uso una tabla intermedia para mantener limpia la relacion unidireccional
    @OneToMany
    @JoinColumn(name = "veterinario_id") 
    private List<Especialidad> especialidades = new ArrayList<>();

    // mapeo 
    @ElementCollection
    @CollectionTable(name = "veterinario_dias_disponibles", joinColumns = @JoinColumn(name = "veterinario_id"))
    @Column(name = "dia")
    private List<String> diasDisponibles = new ArrayList<>();

    // Constructor vacio
    public Veterinario() {
        super();
    }

    // Constructor heredando de Persona
    public Veterinario(String nombre, String apellido, String telefono, String email, String matricula) {
        super(nombre, apellido, telefono, email);
        this.matricula = matricula;
    }

    // Metodos del UML
    public String getEspecialidad() {
        if (especialidades.isEmpty()) {
            return "Sin especialidades asignadas.";
        }
        StringBuilder sb = new StringBuilder();
        for (Especialidad e : especialidades) {
            sb.append(e.getNombreEspecialidad()).append(", ");
        }
        return sb.substring(0, sb.length() - 2); // saca la ultima coma
    }

    public List<String> getDiasDisponibles() {
        return this.diasDisponibles;
    }

    public void agregarEspecialidad(Especialidad especialidad) {
        if (especialidad != null && !this.especialidades.contains(especialidad)) {
            this.especialidades.add(especialidad);
        }
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    // Getters y Setters necesarios
    public String getMatricula() {
        return matricula;
    }

    public List<Especialidad> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<Especialidad> especialidades) {
        this.especialidades = especialidades;
    }

    public void setDiasDisponibles(List<String> diasDisponibles) {
        this.diasDisponibles = diasDisponibles;
    }
    
    
    public boolean validarDisponibilidad(java.time.LocalDate fecha, java.time.LocalTime hora, double duracion) {
        return true;
    }
}