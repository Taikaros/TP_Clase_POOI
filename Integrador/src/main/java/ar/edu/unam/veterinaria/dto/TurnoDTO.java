package ar.edu.unam.veterinaria.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TurnoDTO {
    private Long id;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;
    private Long idMascota;
    private String nombreMascota;
    private Long idVeterinario;
    private String nombreVeterinario;
    private Long idCliente;
    private String nombreCliente;
    private String detallesServicios; 
    private List<String> serviciosSeleccionados; 
    private String notas; 
    private Double costoTotal;
    private Long idVacuna;
    private String nombreVacuna;

    public TurnoDTO() {}

    public TurnoDTO(Long id, LocalDate fecha, LocalTime hora, String estado, Long idMascota, String nombreMascota, Long idVeterinario, String nombreVeterinario, Long idCliente, String nombreCliente, String detallesServicios, List<String> serviciosSeleccionados, String notas, Double costoTotal, Long idVacuna, String nombreVacuna) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.idMascota = idMascota;
        this.nombreMascota = nombreMascota;
        this.idVeterinario = idVeterinario;
        this.nombreVeterinario = nombreVeterinario;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.detallesServicios = detallesServicios;
        this.serviciosSeleccionados = serviciosSeleccionados;
        this.notas = notas;
        this.costoTotal = costoTotal;
        this.idVacuna = idVacuna;
        this.nombreVacuna = nombreVacuna;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Long getIdMascota() { return idMascota; }
    public void setIdMascota(Long idMascota) { this.idMascota = idMascota; }
    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }
    public Long getIdVeterinario() { return idVeterinario; }
    public void setIdVeterinario(Long idVeterinario) { this.idVeterinario = idVeterinario; }
    public String getNombreVeterinario() { return nombreVeterinario; }
    public void setNombreVeterinario(String nombreVeterinario) { this.nombreVeterinario = nombreVeterinario; }
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getDetallesServicios() { return detallesServicios; }
    public void setDetallesServicios(String detallesServicios) { this.detallesServicios = detallesServicios; }
    public List<String> getServiciosSeleccionados() { return serviciosSeleccionados; }
    public void setServiciosSeleccionados(List<String> serviciosSeleccionados) { this.serviciosSeleccionados = serviciosSeleccionados; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public Double getCostoTotal() { return costoTotal; }
    public void setCostoTotal(Double costoTotal) { this.costoTotal = costoTotal; }
    public Long getIdVacuna() { return idVacuna; }
    public void setIdVacuna(Long idVacuna) { this.idVacuna = idVacuna; }
    public String getNombreVacuna() { return nombreVacuna; }
    public void setNombreVacuna(String nombreVacuna) { this.nombreVacuna = nombreVacuna; }
}