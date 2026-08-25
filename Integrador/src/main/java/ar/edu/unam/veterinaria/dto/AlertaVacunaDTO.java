package ar.edu.unam.veterinaria.dto;

import java.time.LocalDate;

public class AlertaVacunaDTO {
    private Long idMascota;
    private String nombreMascota;
    private String nombrePropietario;
    private String telefonoPropietario;
    private String nombreVacuna;
    private LocalDate fechaVencimiento;
    private long diasRestantes;

    public AlertaVacunaDTO(Long idMascota, String nombreMascota, String nombrePropietario, String telefonoPropietario, String nombreVacuna, LocalDate fechaVencimiento, long diasRestantes) {
        this.idMascota = idMascota;
        this.nombreMascota = nombreMascota;
        this.nombrePropietario = nombrePropietario;
        this.telefonoPropietario = telefonoPropietario;
        this.nombreVacuna = nombreVacuna;
        this.fechaVencimiento = fechaVencimiento;
        this.diasRestantes = diasRestantes;
    }

    // Getters
    public Long getIdMascota() { return idMascota; }
    public String getNombreMascota() { return nombreMascota; }
    public String getNombrePropietario() { return nombrePropietario; }
    public String getTelefonoPropietario() { return telefonoPropietario; }
    public String getNombreVacuna() { return nombreVacuna; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public long getDiasRestantes() { return diasRestantes; }
}