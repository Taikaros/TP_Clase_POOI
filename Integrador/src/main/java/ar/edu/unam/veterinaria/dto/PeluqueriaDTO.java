package ar.edu.unam.veterinaria.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class PeluqueriaDTO {
    private Long idTurno;
    private Long idServicio;
    private LocalDate fecha;
    private LocalTime hora;
    private LocalDate fechaNacimiento;
    private String mascotaNombre;
    private String clienteNombre;
    private String servicio;
    private String estado;
    private String especie;
    private String raza;
    private String observaciones;
    private Long numeroFicha;
    private Double costoTotal; // NUEVO CAMPO

    public PeluqueriaDTO() {}

    public Long getIdTurno() { return idTurno; }
    public void setIdTurno(Long idTurno) { this.idTurno = idTurno; }
    public Long getIdServicio() { return idServicio; }
    public void setIdServicio(Long idServicio) { this.idServicio = idServicio; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getMascotaNombre() { return mascotaNombre; }
    public void setMascotaNombre(String mascotaNombre) { this.mascotaNombre = mascotaNombre; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getServicio() { return servicio; }
    public void setServicio(String servicio) { this.servicio = servicio; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Long getNumeroFicha() { return numeroFicha; }
    public void setNumeroFicha(Long numeroFicha) { this.numeroFicha = numeroFicha; }
    public Double getCostoTotal() { return costoTotal; }
    public void setCostoTotal(Double costoTotal) { this.costoTotal = costoTotal; }
}