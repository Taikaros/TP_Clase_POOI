package ar.edu.unam.veterinaria.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class GuarderiaDTO {
    private Long idTurno;
    private Long idServicio;
    private String mascotaNombre;
    private String clienteNombre;
    private String jaula;
    private LocalDate fechaIngreso;
    private LocalTime horaIngreso;
    private LocalDate fechaSalida;
    private LocalDate fechaNacimiento;
    private String tiempo;
    private String especie;
    private String raza;
    private String observaciones;
    private Long numeroFicha;
    private String estadoTurno;
    
    // NUEVOS CAMPOS
    private String alimentacionEspecifica;
    private boolean requiereActividad;
    private Double costoTotal;

    public GuarderiaDTO() {}

    public Long getIdTurno() { return idTurno; }
    public void setIdTurno(Long idTurno) { this.idTurno = idTurno; }
    public Long getIdServicio() { return idServicio; }
    public void setIdServicio(Long idServicio) { this.idServicio = idServicio; }
    public String getMascotaNombre() { return mascotaNombre; }
    public void setMascotaNombre(String mascotaNombre) { this.mascotaNombre = mascotaNombre; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getJaula() { return jaula; }
    public void setJaula(String jaula) { this.jaula = jaula; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public LocalTime getHoraIngreso() { return horaIngreso; }
    public void setHoraIngreso(LocalTime horaIngreso) { this.horaIngreso = horaIngreso; }
    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getTiempo() { return tiempo; }
    public void setTiempo(String tiempo) { this.tiempo = tiempo; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Long getNumeroFicha() { return numeroFicha; }
    public void setNumeroFicha(Long numeroFicha) { this.numeroFicha = numeroFicha; }
    public String getEstadoTurno() { return estadoTurno; }
    public void setEstadoTurno(String estadoTurno) { this.estadoTurno = estadoTurno; }
    
    public String getAlimentacionEspecifica() { return alimentacionEspecifica; }
    public void setAlimentacionEspecifica(String alimentacionEspecifica) { this.alimentacionEspecifica = alimentacionEspecifica; }
    public boolean isRequiereActividad() { return requiereActividad; }
    public void setRequiereActividad(boolean requiereActividad) { this.requiereActividad = requiereActividad; }
    public Double getCostoTotal() { return costoTotal; }
    public void setCostoTotal(Double costoTotal) { this.costoTotal = costoTotal; }
}