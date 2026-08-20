package ar.edu.unam.veterinaria.dto;

import java.time.LocalDate;

public class MascotaDTO {
    private long id;
    private String nombreMascota;
    private String especie;
    private String raza;
    private LocalDate fechaNacimiento;

    private long idCliente; // Agregar el atributo idCliente
    private String nombreDueno; // Agregar el atributo nombreDueño
    
    public MascotaDTO() {
    }

    public MascotaDTO(long id, String nombreMascota, String especie, String raza, LocalDate fechaNacimiento, long idCliente, String nombreDueno) {
        this.id = id;
        this.nombreMascota = nombreMascota;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.idCliente = idCliente; // Inicializar el atributo idCliente
        this.nombreDueno = nombreDueno; // Inicializar el atributo nombreDueño
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getNombreMascota() {
        return nombreMascota;
    }
    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota; 
    }
    public String getEspecie() {
        return especie;
    }
    public void setEspecie(String especie) {
        this.especie = especie;
    }
    public String getRaza() {
        return raza;
    }
    public void setRaza(String raza) {
        this.raza = raza;
    }
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    public long getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(long idCliente) {
        this.idCliente = idCliente;
    }
    public String getNombreDueno() {
        return nombreDueno;
    }
    public void setNombreDueno(String nombreDueno) {
        this.nombreDueno = nombreDueno;
    }

}
