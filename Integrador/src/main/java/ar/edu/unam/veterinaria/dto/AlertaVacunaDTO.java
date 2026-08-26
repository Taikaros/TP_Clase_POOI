package ar.edu.unam.veterinaria.dto;

import java.time.LocalDate;

public class AlertaVacunaDTO {
    private Long idMascota;
    private Long idCliente; 
    private String nombreMascota;
    private String nombrePropietario;
    private String telefonoPropietario;
    private String nombreVacuna;
    private String enfermedadVacuna;
    private LocalDate fechaVencimiento;
    private long diasRestantes;

    public AlertaVacunaDTO(Long idMascota, Long idCliente, String nombreMascota, String nombrePropietario, String telefonoPropietario, String nombreVacuna, String enfermedadVacuna, LocalDate fechaVencimiento, long diasRestantes) {
        this.idMascota = idMascota;
        this.idCliente = idCliente;
        this.nombreMascota = nombreMascota;
        this.nombrePropietario = nombrePropietario;
        this.telefonoPropietario = telefonoPropietario;
        this.nombreVacuna = nombreVacuna;
        this.enfermedadVacuna = enfermedadVacuna;
        this.fechaVencimiento = fechaVencimiento;
        this.diasRestantes = diasRestantes;
    }

    public Long getIdMascota() { return idMascota; }
    public Long getIdCliente() { return idCliente; } 
    public String getNombreMascota() { return nombreMascota; }
    public String getNombrePropietario() { return nombrePropietario; }
    public String getTelefonoPropietario() { return telefonoPropietario; }
    public String getNombreVacuna() { return nombreVacuna; }
    public String getEnfermedadVacuna() { return enfermedadVacuna; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public long getDiasRestantes() { return diasRestantes; }
}