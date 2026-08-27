package ar.edu.unam.veterinaria.dto;

import java.time.LocalDate;

public class MascotaDTO {
    private Long id;
    private String nombreMascota;
    private String especie;
    private String raza;
    private LocalDate fechaNacimiento;
    private Long numeroFicha; // Atributo agregado
    private Long idCliente; 
    private String nombreDueno; 


    public MascotaDTO() {
    }

    public MascotaDTO(Long id, String nombreMascota, String especie, String raza, LocalDate fechaNacimiento, Long idCliente, String nombreDueno, Long numeroFicha) {
        this.id = id;
        this.nombreMascota = nombreMascota;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.idCliente = idCliente; 
        this.nombreDueno = nombreDueno; 
        this.numeroFicha = numeroFicha;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
    public String getNombreDueno() { return nombreDueno; }
    public void setNombreDueno(String nombreDueno) { this.nombreDueno = nombreDueno; }
    public Long getNumeroFicha() { return numeroFicha; }
    public void setNumeroFicha(Long numeroFicha) { this.numeroFicha = numeroFicha; }
}
