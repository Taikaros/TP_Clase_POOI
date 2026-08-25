package ar.edu.unam.veterinaria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "servicio_consulta")
public class Consulta extends Servicio {

    private String motivoConsulta;
    private String diagnostico;
    private String tratamiento;

    public Consulta() {}

    public String getDetallesConsulta() {
        String dx = (diagnostico != null && !diagnostico.isEmpty()) ? diagnostico : "Pendiente";
        String tr = (tratamiento != null && !tratamiento.isEmpty()) ? tratamiento : "Sin indicaciones específicas";
        return "Consulta: " + motivoConsulta + " | Dx: " + dx + " | R/: " + tr;
    }

    @Override
    public String getDetallesServicio() {
        return getDetallesConsulta();
    }

    public String getMotivoConsulta() { return motivoConsulta; }
    public void setMotivoConsulta(String motivoConsulta) { this.motivoConsulta = motivoConsulta; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }
}