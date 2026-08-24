package ar.edu.unam.veterinaria.service;

import ar.edu.unam.veterinaria.AppVeterinaria;
import ar.edu.unam.veterinaria.dto.TurnoDTO;
import ar.edu.unam.veterinaria.exception.CancelacionFueradeTermino;
import ar.edu.unam.veterinaria.exception.TurnoSolapado;
import ar.edu.unam.veterinaria.exception.VeterinarioNoDisponible;
import ar.edu.unam.veterinaria.mapper.TurnoMapper;
import ar.edu.unam.veterinaria.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TurnoService {

    public List<TurnoDTO> obtenerTodos() {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            // EL FIX MÁGICO: Usamos LEFT JOIN FETCH para traer todo en 1 solo viaje a la base de datos
            String jpql = "SELECT DISTINCT t FROM Turno t " +
                          "LEFT JOIN FETCH t.cliente " +
                          "LEFT JOIN FETCH t.mascota " +
                          "LEFT JOIN FETCH t.veterinario " +
                          "LEFT JOIN FETCH t.servicios " +
                          "ORDER BY t.fecha DESC, t.hora DESC";
                          
            TypedQuery<Turno> query = em.createQuery(jpql, Turno.class);
            return query.getResultStream().map(TurnoMapper::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }
    private void validarSolapamiento(EntityManager em, TurnoDTO dto, Long turnoIdIgnorar) throws TurnoSolapado {
        String jpql = "SELECT COUNT(t) FROM Turno t WHERE t.fecha = :fecha AND t.hora = :hora AND t.estado != 'CANCELADO' " +
                      "AND (t.veterinario.id = :vetId OR t.mascota.id = :masId)";
        if (turnoIdIgnorar != null) jpql += " AND t.id != :turnoIdIgnorar";

        TypedQuery<Long> query = em.createQuery(jpql, Long.class)
            .setParameter("fecha", dto.getFecha())
            .setParameter("hora", dto.getHora())
            .setParameter("vetId", dto.getIdVeterinario())
            .setParameter("masId", dto.getIdMascota());
            
        if (turnoIdIgnorar != null) query.setParameter("turnoIdIgnorar", turnoIdIgnorar);

        if (query.getSingleResult() > 0) {
            throw new TurnoSolapado("Ya existe un turno agendado en ese horario para el veterinario o la mascota.");
        }
    }

    // EL AUTOGENERADOR DE CATÁLOGOS (Si el precio no existe, lo crea)
    private TipoServicio obtenerOCrearTipoServicio(EntityManager em, String nombre) {
        List<TipoServicio> catalogo = em.createQuery("SELECT ts FROM TipoServicio ts WHERE ts.nombreDescriptivo = :nombre", TipoServicio.class)
            .setParameter("nombre", nombre).getResultList();
            
        if (!catalogo.isEmpty()) return catalogo.get(0);
        
        Double precioFicticio = 15000.0;
        if (nombre.contains("Vacunación")) precioFicticio = 8500.0;
        if (nombre.contains("Cirugía")) precioFicticio = 45000.0;
        if (nombre.contains("Ecografía")) precioFicticio = 22000.0;
        if (nombre.contains("Análisis")) precioFicticio = 18000.0;
        
        TipoServicio nuevoTS = new TipoServicio(nombre, precioFicticio, 30.0, 10);
        em.persist(nuevoTS);
        return nuevoTS;
    }

    public TurnoDTO guardarTurno(TurnoDTO dto) throws VeterinarioNoDisponible, TurnoSolapado {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            validarSolapamiento(em, dto, null);
            
            Turno turno = new Turno();
            turno.setFecha(dto.getFecha());
            turno.setHora(dto.getHora());
            turno.setEstado(EstadoTurno.PENDIENTE); 
            
            Cliente cliente = Optional.ofNullable(em.find(Cliente.class, dto.getIdCliente())).orElseThrow();
            Mascota mascota = Optional.ofNullable(em.find(Mascota.class, dto.getIdMascota())).orElseThrow();
            Veterinario veterinario = Optional.ofNullable(em.find(Veterinario.class, dto.getIdVeterinario())).orElseThrow();
            
            if (!veterinario.validarDisponibilidad(dto.getFecha(), dto.getHora(), 30.0)) {
                throw new VeterinarioNoDisponible("El profesional no atiende en el día y horario seleccionado.");
            }
            
            turno.setCliente(cliente);
            turno.setMascota(mascota);
            turno.setVeterinario(veterinario);

            if (dto.getServiciosSeleccionados() != null) {
                for (String nombreServicio : dto.getServiciosSeleccionados()) {
                    TipoServicio ts = obtenerOCrearTipoServicio(em, nombreServicio); // <--- Busca el precio
                    if (nombreServicio.equals("Vacunación")) {
                        Vacunacion vacunacion = new Vacunacion();
                        vacunacion.setTipoServicio(ts); // <--- Congela el precio histórico
                        turno.agregarServicio(vacunacion);
                    } else {
                        Consulta consulta = new Consulta();
                        consulta.setTipoServicio(ts); // <--- Congela el precio histórico
                        consulta.setMotivoConsulta(nombreServicio + (dto.getNotas() != null && !dto.getNotas().isEmpty() ? " - " + dto.getNotas() : ""));
                        turno.agregarServicio(consulta);
                    }
                }
            }
            
            em.persist(turno);
            em.getTransaction().commit();
            return TurnoMapper.toDTO(turno);
        } catch (TurnoSolapado | VeterinarioNoDisponible e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e; 
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public TurnoDTO actualizarTurno(TurnoDTO dto) throws VeterinarioNoDisponible, TurnoSolapado {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            Turno turno = Optional.ofNullable(em.find(Turno.class, dto.getId())).orElseThrow();
            validarSolapamiento(em, dto, turno.getId()); 
            
            turno.setFecha(dto.getFecha());
            turno.setHora(dto.getHora());
            
            Cliente cliente = Optional.ofNullable(em.find(Cliente.class, dto.getIdCliente())).orElseThrow();
            Mascota mascota = Optional.ofNullable(em.find(Mascota.class, dto.getIdMascota())).orElseThrow();
            Veterinario veterinario = Optional.ofNullable(em.find(Veterinario.class, dto.getIdVeterinario())).orElseThrow();
            
            if (!veterinario.validarDisponibilidad(dto.getFecha(), dto.getHora(), 30.0)) {
                throw new VeterinarioNoDisponible("El profesional no atiende en el día y horario seleccionado.");
            }

            turno.setCliente(cliente);
            turno.setMascota(mascota);
            turno.setVeterinario(veterinario);
            
            turno.getServicios().clear();
            em.flush(); 

            if (dto.getServiciosSeleccionados() != null) {
                for (String nombreServicio : dto.getServiciosSeleccionados()) {
                    TipoServicio ts = obtenerOCrearTipoServicio(em, nombreServicio);
                    if (nombreServicio.equals("Vacunación")) {
                        Vacunacion vacunacion = new Vacunacion();
                        vacunacion.setTipoServicio(ts);
                        turno.agregarServicio(vacunacion);
                    } else {
                        Consulta consulta = new Consulta();
                        consulta.setTipoServicio(ts);
                        consulta.setMotivoConsulta(nombreServicio);
                        turno.agregarServicio(consulta);
                    }
                }
            }
            em.merge(turno);
            em.getTransaction().commit();
            return TurnoMapper.toDTO(turno);
        } catch (TurnoSolapado | VeterinarioNoDisponible e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public void confirmarTurno(Long idTurno) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try { em.getTransaction().begin(); Turno turno = em.find(Turno.class, idTurno); if (turno != null) { turno.setEstado(EstadoTurno.CONFIRMADO); em.merge(turno); } em.getTransaction().commit(); } catch (Exception e) { if (em.getTransaction().isActive()) em.getTransaction().rollback(); e.printStackTrace(); } finally { em.close(); }
    }

    public void atenderTurno(Long idTurno) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try { em.getTransaction().begin(); Turno turno = em.find(Turno.class, idTurno); if (turno != null) { turno.setEstado(EstadoTurno.ATENDIDO); em.merge(turno); } em.getTransaction().commit(); } catch (Exception e) { if (em.getTransaction().isActive()) em.getTransaction().rollback(); e.printStackTrace(); } finally { em.close(); }
    }

    public void cancelarTurno(Long idTurno) throws CancelacionFueradeTermino {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            Turno turno = em.find(Turno.class, idTurno);
            if (turno != null) {
                java.time.LocalDateTime fechaHoraTurno = java.time.LocalDateTime.of(turno.getFecha(), turno.getHora());
                if (java.time.LocalDateTime.now().plusHours(24).isAfter(fechaHoraTurno)) {
                    throw new CancelacionFueradeTermino("El turno solo puede cancelarse con al menos 24 horas de anticipación.");
                }
                turno.setEstado(EstadoTurno.CANCELADO);
                em.merge(turno);
            }
            em.getTransaction().commit();
        } catch (CancelacionFueradeTermino e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e; 
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally { em.close(); }
    }
}