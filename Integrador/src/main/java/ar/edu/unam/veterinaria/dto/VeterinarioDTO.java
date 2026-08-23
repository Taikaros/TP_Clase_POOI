package ar.edu.unam.veterinaria.dto;

import java.util.List;

public class VeterinarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String matricula;
    private List<EspecialidadDTO> especialidades;
    private List<String> diasDisponibles; // Agregamos los horarios

    public VeterinarioDTO() {}

    public VeterinarioDTO(Long id, String nombre, String apellido, String telefono, String email, String matricula, List<EspecialidadDTO> especialidades, List<String> diasDisponibles) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.matricula = matricula;
        this.especialidades = especialidades;
        this.diasDisponibles = diasDisponibles;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public List<EspecialidadDTO> getEspecialidades() { return especialidades; }
    public void setEspecialidades(List<EspecialidadDTO> especialidades) { this.especialidades = especialidades; }
    public List<String> getDiasDisponibles() { return diasDisponibles; }
    public void setDiasDisponibles(List<String> diasDisponibles) { this.diasDisponibles = diasDisponibles; }
}