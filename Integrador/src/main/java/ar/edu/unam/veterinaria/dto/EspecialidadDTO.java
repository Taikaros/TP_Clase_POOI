package ar.edu.unam.veterinaria.dto;

public class EspecialidadDTO {
    private Long id;
    private String nombreEspecialidad;
    private String descripcion;

    public EspecialidadDTO() {}

    public EspecialidadDTO(Long id, String nombreEspecialidad, String descripcion) {
        this.id = id;
        this.nombreEspecialidad = nombreEspecialidad;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreEspecialidad() { return nombreEspecialidad; }
    public void setNombreEspecialidad(String nombreEspecialidad) { this.nombreEspecialidad = nombreEspecialidad; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}