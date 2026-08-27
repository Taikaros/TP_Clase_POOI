package ar.edu.unam.veterinaria.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "veterinarios")
public class Veterinario extends Persona {

    @Column(unique = true, nullable = false)
    private String matricula;

    @ManyToMany
    @JoinTable(
        name = "veterinario_especialidad",
        joinColumns = @JoinColumn(name = "veterinario_id"),
        inverseJoinColumns = @JoinColumn(name = "especialidad_id")
    )
    private List<Especialidad> especialidades = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "veterinario_dias_disponibles")
    private List<String> diasDisponibles = new ArrayList<>();

    public Veterinario() {}

    public Veterinario(String nombre, String apellido, String telefono, String email, String matricula) {
        super(nombre, apellido, telefono, email);
        setMatricula(matricula);
    }

    public String getMatricula() { return matricula; }

    public void setMatricula(String matricula) {
        if (matricula == null || matricula.trim().isEmpty()) throw new IllegalArgumentException("La matrícula es obligatoria.");
        this.matricula = matricula;
    }

    public List<Especialidad> getEspecialidades() { return especialidades; }

    public void setEspecialidades(List<Especialidad> especialidades) {
        if (especialidades == null) throw new IllegalArgumentException("La lista de especialidades no puede ser nula.");
        this.especialidades = especialidades;
    }

    public void agregarEspecialidad(Especialidad especialidad) throws ar.edu.unam.veterinaria.exception.EspecialidadExistente {
        if (especialidad == null) throw new IllegalArgumentException("La especialidad no puede ser nula.");
        for (Especialidad e : this.especialidades) {
            if (e.getNombreEspecialidad().equalsIgnoreCase(especialidad.getNombreEspecialidad())) {
                throw new ar.edu.unam.veterinaria.exception.EspecialidadExistente("El veterinario ya posee esta especialidad.");
            }
        }
        this.especialidades.add(especialidad);
    }

    public List<String> getDiasDisponibles() { return diasDisponibles; }

    public void setDiasDisponibles(List<String> diasDisponibles) {
        if (diasDisponibles == null) throw new IllegalArgumentException("Los días disponibles no pueden ser nulos.");
        this.diasDisponibles = diasDisponibles;
    }

    public String getEspecialidad() {
        if (this.especialidades == null || this.especialidades.isEmpty()) return "Sin especialidad";
        return this.especialidades.get(0).getNombreEspecialidad(); // Simplificado para listados
    }

   public boolean validarDisponibilidad(java.time.LocalDate fecha, java.time.LocalTime hora, double duracionMinutos) {
        if (fecha == null || hora == null) return false;
        String diaStr = fecha.getDayOfWeek().toString().toLowerCase();
        String prefijoDia = "";
        String prefijoDiaAlt = ""; // Agregamos prefijo alternativo para lidiar con tildes
        
        switch (diaStr) {
            case "monday": prefijoDia = "Lun:"; break;
            case "tuesday": prefijoDia = "Mar:"; break;
            case "wednesday": prefijoDia = "Mie:"; prefijoDiaAlt = "Mié:"; break;
            case "thursday": prefijoDia = "Jue:"; break;
            case "friday": prefijoDia = "Vie:"; break;
            case "saturday": prefijoDia = "Sab:"; prefijoDiaAlt = "Sáb:"; break;
            case "sunday": prefijoDia = "Dom:"; break;
        }

        for (String horario : this.diasDisponibles) {
            // Verifica tanto la versión sin tilde como con tilde
            if (horario.startsWith(prefijoDia) || (!prefijoDiaAlt.isEmpty() && horario.startsWith(prefijoDiaAlt))) {
                try {
                    String rangoHorario = horario.substring(horario.indexOf(":") + 1).trim();
                    String[] limites = rangoHorario.split("-");
                    java.time.LocalTime inicioJornada = java.time.LocalTime.parse(limites[0]);
                    java.time.LocalTime finJornada = java.time.LocalTime.parse(limites[1]);
                    java.time.LocalTime finTurno = hora.plusMinutes((long) duracionMinutos);

                    if (!hora.isBefore(inicioJornada) && !finTurno.isAfter(finJornada)) {
                        return true;
                    }
                } catch (Exception e) { System.err.println("Error parseando horario."); }
            }
        }
        return false;
    }
}