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
    @Column(nullable = false)
    private EstadoTurno estado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @ManyToOne(optional = false)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    public Turno() {
        this.estado = EstadoTurno.PENDIENTE;
    }

    public void agregarServicio(Servicio servicio) {
        if (servicio == null) throw new IllegalArgumentException("El servicio a agregar no puede ser nulo.");
        if (!this.servicios.contains(servicio)) {
            this.servicios.add(servicio);
            servicio.setTurno(this);
        }
    }

    public Double calcularCostoTotal() {
        if (this.servicios == null || this.servicios.isEmpty()) return 0.0;
        return this.servicios.stream().mapToDouble(Servicio::calcularCosto).sum();
    }

    public void validarServicios() throws TurnoSinServiciosException {
        if (this.servicios == null || this.servicios.isEmpty()) {
            throw new TurnoSinServiciosException("Regla de Dominio: Un turno no puede ser agendado sin servicios.");
        }
    }

    public void confirmar() {
        if (this.estado == EstadoTurno.CANCELADO || this.estado == EstadoTurno.ATENDIDO) {
            throw new IllegalStateException("No se puede confirmar un turno atendido o cancelado.");
        }
        this.estado = EstadoTurno.CONFIRMADO;
    }

    public void atender() {
        if (this.estado == EstadoTurno.CANCELADO) throw new IllegalStateException("No se puede atender un turno cancelado.");
        this.estado = EstadoTurno.ATENDIDO;
    }

    public void cancelar(LocalDateTime fechaHoraActual) throws CancelacionFueradeTermino {
        if (this.estado == EstadoTurno.ATENDIDO) throw new IllegalStateException("Un turno ya atendido no puede cancelarse.");
        if (this.estado == EstadoTurno.CANCELADO) return; 

        if (fechaHoraActual != null) {
            LocalDateTime fechaHoraTurno = LocalDateTime.of(this.fecha, this.hora);
            if (fechaHoraActual.plusHours(24).isAfter(fechaHoraTurno)) {
                throw new CancelacionFueradeTermino("El turno solo puede cancelarse con 24 horas de anticipación.");
            }
        }
        this.estado = EstadoTurno.CANCELADO;
    }

    // Getters
    public Long getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public List<Servicio> getServicios() { return servicios; }
    public EstadoTurno getEstado() { return estado; }
    public Mascota getMascota() { return mascota; }
    public Veterinario getVeterinario() { return veterinario; }
    public Cliente getCliente() { return cliente; }

    // Setters Defensivos
    public void setId(Long id) { this.id = id; }
    
    public void setFecha(LocalDate fecha) {
        if (fecha == null) throw new IllegalArgumentException("La fecha del turno es obligatoria.");
        this.fecha = fecha;
    }

    public void setHora(LocalTime hora) {
        if (hora == null) throw new IllegalArgumentException("La hora del turno es obligatoria.");
        this.hora = hora;
    }

    public void setServicios(List<Servicio> servicios) {
        if (servicios == null) throw new IllegalArgumentException("La lista de servicios no puede ser nula.");
        this.servicios = servicios;
    }

    public void setEstado(EstadoTurno estado) {
        if (estado == null) throw new IllegalArgumentException("El estado es obligatorio.");
        this.estado = estado;
    }

    public void setMascota(Mascota mascota) {
        if (mascota == null) throw new IllegalArgumentException("El turno debe estar asignado a una mascota.");
        this.mascota = mascota;
    }

    public void setVeterinario(Veterinario veterinario) {
        if (veterinario == null) throw new IllegalArgumentException("El turno debe estar asignado a un veterinario.");
        this.veterinario = veterinario;
    }

    public void setCliente(Cliente cliente) {
        if (cliente == null) throw new IllegalArgumentException("El turno debe estar asignado a un cliente.");
        this.cliente = cliente;
    }
}