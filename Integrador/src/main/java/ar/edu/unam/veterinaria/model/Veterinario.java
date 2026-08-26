package ar.edu.unam.veterinaria.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
    @ManyToMany
    @JoinTable(
        name = "veterinario_especialidad",
        joinColumns = @JoinColumn(name = "veterinario_id"),
        inverseJoinColumns = @JoinColumn(name = "especialidad_id")
    )
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

    public void agregarEspecialidad(Especialidad especialidad) throws ar.edu.unam.veterinaria.exception.EspecialidadExistente {
        if (especialidad != null) {
            for (Especialidad e : this.especialidades) {
                if (e.getNombreEspecialidad().equalsIgnoreCase(especialidad.getNombreEspecialidad())) {
                    throw new ar.edu.unam.veterinaria.exception.EspecialidadExistente(
                        "El veterinario ya posee la especialidad: " + especialidad.getNombreEspecialidad()
                    );
                }
            }
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
    
    
public boolean validarDisponibilidad(java.time.LocalDate fecha, java.time.LocalTime hora, double duracionMinutos) {
        if (this.diasDisponibles == null || this.diasDisponibles.isEmpty()) {
            return false;
        }

        // Traducimos la fecha actual al formato de texto que usamos en la vista (Lun, Mar, Mié, etc.)
        String prefijoDia = "";
        switch (fecha.getDayOfWeek()) {
            case MONDAY: prefijoDia = "Lun"; break;
            case TUESDAY: prefijoDia = "Mar"; break;
            case WEDNESDAY: prefijoDia = "Mi"; break; // Buscamos por prefijo "Mi" para evitar problemas con la tilde
            case THURSDAY: prefijoDia = "Jue"; break;
            case FRIDAY: prefijoDia = "Vie"; break;
            case SATURDAY: prefijoDia = "S"; break; // "Sáb" o "Sab"
            case SUNDAY: prefijoDia = "Dom"; break;
        }

        for (String diaStr : this.diasDisponibles) {
            if (diaStr.startsWith(prefijoDia)) {
                try {
                    // Extraemos el horario, ej: "Lun: 09:00-17:00" -> "09:00-17:00"
                    String rangoHorario = diaStr.substring(diaStr.indexOf(":") + 1).trim();
                    String[] limites = rangoHorario.split("-");
                    
                    if (limites.length == 2) {
                        java.time.LocalTime inicioJornada = java.time.LocalTime.parse(limites[0].trim());
                        java.time.LocalTime finJornada = java.time.LocalTime.parse(limites[1].trim());
                        
                        // Calculamos a qué hora terminará el turno
                        java.time.LocalTime finTurno = hora.plusMinutes((long) duracionMinutos);

                        // Verificamos que no empiece antes de que llegue, ni termine después de que se vaya
                        if (!hora.isBefore(inicioJornada) && !finTurno.isAfter(finJornada)) {
                            return true; // El horario es válido
                        }
                    }
                } catch (Exception e) {
                    // Si el horario se escribió mal en la BD (ej. 09 a 17), salta el error y no da disponibilidad
                    System.out.println("Error parseando horario del veterinario: " + diaStr);
                }
            }
        }
        return false; // Si recorrió todo y no encontró un día válido, no está disponible
    }
}