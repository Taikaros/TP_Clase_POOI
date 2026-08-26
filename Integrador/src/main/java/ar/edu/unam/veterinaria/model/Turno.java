package ar.edu.unam.veterinaria.model;

import ar.edu.unam.veterinaria.exception.CancelacionFueradeTermino;
import ar.edu.unam.veterinaria.exception.TurnoSinServiciosException;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "turnos")
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @OneToMany(mappedBy = "turno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Servicio> servicios = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EstadoTurno estado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mascota_id")
    private Mascota mascota;

    @ManyToOne(optional = false)
    @JoinColumn(name = "veterinario_id")
    private Veterinario veterinario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    public Turno() {
        this.estado = EstadoTurno.PENDIENTE;
    }

    public void agregarServicio(Servicio servicio) {
        if (servicio != null && !this.servicios.contains(servicio)) {
            this.servicios.add(servicio);
            servicio.setTurno(this);
        }
    }

    public Double calcularCostoTotal() {
        if (this.servicios == null || this.servicios.isEmpty()) {
            return 0.0;
        }
        return this.servicios.stream()
                .mapToDouble(Servicio::calcularCosto)
                .sum();
    }

    public void validarServicios() throws TurnoSinServiciosException {
        if (this.servicios == null || this.servicios.isEmpty()) {
            throw new TurnoSinServiciosException(
                "Regla de Dominio: Un turno no puede ser agendado sin al menos una práctica o servicio asociado."
            );
        }
    }

    // ---> MÁQUINA DE ESTADOS Y CANCELACIÓN EN EL DOMINIO <---

    public void confirmar() {
        if (this.estado == EstadoTurno.CANCELADO || this.estado == EstadoTurno.ATENDIDO) {
            throw new IllegalStateException("No se puede confirmar un turno que ya fue atendido o cancelado.");
        }
        this.estado = EstadoTurno.CONFIRMADO;
    }

    public void atender() {
        if (this.estado == EstadoTurno.CANCELADO) {
            throw new IllegalStateException("No se puede atender un turno cancelado.");
        }
        if (this.estado == EstadoTurno.ATENDIDO) {
            throw new IllegalStateException("El turno ya se encuentra atendido.");
        }
        this.estado = EstadoTurno.ATENDIDO;
    }

    public void cancelar(LocalDateTime fechaHoraActual) throws CancelacionFueradeTermino {
        if (this.estado == EstadoTurno.ATENDIDO) {
            throw new IllegalStateException("Un turno ya atendido no puede ser cancelado.");
        }
        if (this.estado == EstadoTurno.CANCELADO) {
            return; 
        }

        LocalDateTime fechaHoraTurno = LocalDateTime.of(this.fecha, this.hora);
        if (fechaHoraActual.plusHours(24).isAfter(fechaHoraTurno)) {
            throw new CancelacionFueradeTermino(
                "El turno solo puede cancelarse con al menos 24 horas de anticipación."
            );
        }
        this.estado = EstadoTurno.CANCELADO;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public List<Servicio> getServicios() { return servicios; }
    public void setServicios(List<Servicio> servicios) { this.servicios = servicios; }
    public EstadoTurno getEstado() { return estado; }
    public void setEstado(EstadoTurno estado) { this.estado = estado; }
    public Mascota getMascota() { return mascota; }
    public void setMascota(Mascota mascota) { this.mascota = mascota; }
    public Veterinario getVeterinario() { return veterinario; }
    public void setVeterinario(Veterinario veterinario) { this.veterinario = veterinario; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
}