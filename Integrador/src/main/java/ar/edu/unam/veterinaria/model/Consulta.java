package ar.edu.unam.veterinaria.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "servicio_consulta")
public class Consulta extends Servicio {

    @Column(nullable = false)
    private String motivoConsulta;

    @Column(nullable = true)
    private String diagnostico;

    @Column(nullable = true)
    private String tratamiento;

    public Consulta() {
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        if (motivoConsulta == null || motivoConsulta.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de la consulta es obligatorio.");
        }
        this.motivoConsulta = motivoConsulta;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    @Override
    public String getDetallesServicio() {
        // SOLUCIÓN: Quitamos el prefijo "Consulta: " que estaba hardcodeado
        String base = this.motivoConsulta; 
        if (this.diagnostico != null && !this.diagnostico.trim().isEmpty()) {
            base = base + " (Atendida)";
        }
        return base;
    }
}