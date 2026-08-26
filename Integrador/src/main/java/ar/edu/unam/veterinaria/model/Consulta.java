package ar.edu.unam.veterinaria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "servicio_consulta")
public class Consulta extends Servicio {

    @Column(nullable = false)
    private String motivoConsulta;

    private String diagnostico;
    private String tratamiento;

    public Consulta() {}

    public String getMotivoConsulta() { return motivoConsulta; }
    public void setMotivoConsulta(String motivoConsulta) {
        if (motivoConsulta == null || motivoConsulta.trim().isEmpty()) throw new IllegalArgumentException("El motivo de la consulta es obligatorio.");
        this.motivoConsulta = motivoConsulta;
    }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico != null ? diagnostico.trim() : "";
    }

    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento != null ? tratamiento.trim() : "";
    }

    @Override
    public String getDetallesServicio() {
        return "Consulta: " + this.motivoConsulta;
    }
}