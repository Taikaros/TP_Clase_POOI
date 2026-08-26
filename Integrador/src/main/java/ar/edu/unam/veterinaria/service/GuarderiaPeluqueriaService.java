package ar.edu.unam.veterinaria.service;

import ar.edu.unam.veterinaria.AppVeterinaria;
import ar.edu.unam.veterinaria.dto.GuarderiaDTO;
import ar.edu.unam.veterinaria.dto.PeluqueriaDTO;
import ar.edu.unam.veterinaria.mapper.GuarderiaPeluqueriaMapper;
import ar.edu.unam.veterinaria.model.*;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class GuarderiaPeluqueriaService {

    public List<GuarderiaDTO> obtenerGuarderiasActivas() {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            String jpql = "SELECT t FROM Turno t JOIN FETCH t.servicios s WHERE TYPE(s) = Guarderia AND t.estado != 'CANCELADO'";
            List<Turno> turnos = em.createQuery(jpql, Turno.class).getResultList();
            
            return turnos.stream().flatMap(t -> t.getServicios().stream()
                    .filter(s -> s instanceof Guarderia)
                    .map(s -> GuarderiaPeluqueriaMapper.toGuarderiaDTO(t, (Guarderia) s)))
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public List<PeluqueriaDTO> obtenerPeluqueriasDelDia() {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            String jpql = "SELECT t FROM Turno t JOIN FETCH t.servicios s WHERE TYPE(s) = Peluqueria AND t.estado != 'CANCELADO' ORDER BY t.hora ASC";
            List<Turno> turnos = em.createQuery(jpql, Turno.class).getResultList();
            
            return turnos.stream().flatMap(t -> t.getServicios().stream()
                    .filter(s -> s instanceof Peluqueria)
                    .map(s -> GuarderiaPeluqueriaMapper.toPeluqueriaDTO(t, (Peluqueria) s)))
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    private TipoServicio obtenerOCrearTipoServicio(EntityManager em, String nombre) {
        List<TipoServicio> catalogo = em.createQuery("SELECT ts FROM TipoServicio ts WHERE ts.nombreDescriptivo = :nombre", TipoServicio.class)
                .setParameter("nombre", nombre).getResultList();
        if (!catalogo.isEmpty()) return catalogo.get(0);
        TipoServicio nuevoTS = new TipoServicio(nombre, 8000.0, 60.0, 10);
        em.persist(nuevoTS);
        return nuevoTS;
    }

    public void registrarGuarderia(Long idCliente, Long idMascota, java.time.LocalDate fecha, java.time.LocalTime hora, java.time.LocalDate fechaSalida, String jaula, String alimentacion, boolean actividad, String observaciones) throws ar.edu.unam.veterinaria.exception.CupoLLeno, ar.edu.unam.veterinaria.exception.JaulaNoDisponible {
        jakarta.persistence.EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            ar.edu.unam.veterinaria.model.Cliente cliente = em.find(ar.edu.unam.veterinaria.model.Cliente.class, idCliente);
            ar.edu.unam.veterinaria.model.Mascota mascota = em.find(ar.edu.unam.veterinaria.model.Mascota.class, idMascota);
            
            // Asignamos a un veterinario de guardia genérico (el ID 1)
            ar.edu.unam.veterinaria.model.Veterinario veterinario = em.find(ar.edu.unam.veterinaria.model.Veterinario.class, 1L);

            ar.edu.unam.veterinaria.model.TipoServicio tsBd = obtenerOCrearTipoServicio(em, "Guardería");

            // ---> DEFENSA 1: Validar Cupos Diarios <---
            Long ocupacionActual = em.createQuery("SELECT COUNT(s) FROM Servicio s JOIN s.turno t WHERE s.tipoServicio.id = :tsId AND t.fecha = :fecha AND t.estado != 'CANCELADO'", Long.class)
                    .setParameter("tsId", tsBd.getId())
                    .setParameter("fecha", fecha)
                    .getSingleResult();
            tsBd.validarCupo(fecha, ocupacionActual.intValue()); // El dominio evalúa y lanza CupoLLeno si hace falta

            // ---> DEFENSA 2: Validar Disponibilidad de Jaulas <---
            java.util.List<String> jaulasOcupadas = em.createQuery("SELECT g.jaulaAsignada FROM Guarderia g JOIN g.turno t WHERE t.fecha = :fecha AND t.estado != 'CANCELADO'", String.class)
                    .setParameter("fecha", fecha)
                    .getResultList();

            ar.edu.unam.veterinaria.model.Guarderia guarderia = new ar.edu.unam.veterinaria.model.Guarderia();
            guarderia.registrarReserva(mascota, fecha, jaula, jaulasOcupadas); // El dominio evalúa y lanza JaulaNoDisponible si hace falta

            guarderia.setTipoServicio(tsBd);
            guarderia.setFechaSalida(fechaSalida);
            guarderia.setAlimentacionEspecifica(alimentacion);
            guarderia.setRequiereActividad(actividad);
            guarderia.setObservaciones(observaciones);

            ar.edu.unam.veterinaria.model.Turno turno = new ar.edu.unam.veterinaria.model.Turno();
            turno.setCliente(cliente);
            turno.setMascota(mascota);
            turno.setVeterinario(veterinario);
            turno.setFecha(fecha);
            turno.setHora(hora);
            turno.setEstado(ar.edu.unam.veterinaria.model.EstadoTurno.CONFIRMADO);
            turno.agregarServicio(guarderia);

            em.persist(turno);
            em.getTransaction().commit();
        } catch (ar.edu.unam.veterinaria.exception.CupoLLeno | ar.edu.unam.veterinaria.exception.JaulaNoDisponible e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e; // Repasamos la excepción al Controlador
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally { em.close(); }
    }

    public void registrarPeluqueria(Long idCliente, Long idMascota, java.time.LocalDate fecha, java.time.LocalTime hora, ar.edu.unam.veterinaria.model.TipoServicio tipoServicio, String observaciones) throws ar.edu.unam.veterinaria.exception.CupoLLeno {
        jakarta.persistence.EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            ar.edu.unam.veterinaria.model.Cliente cliente = em.find(ar.edu.unam.veterinaria.model.Cliente.class, idCliente);
            ar.edu.unam.veterinaria.model.Mascota mascota = em.find(ar.edu.unam.veterinaria.model.Mascota.class, idMascota);
            ar.edu.unam.veterinaria.model.Veterinario veterinario = em.find(ar.edu.unam.veterinaria.model.Veterinario.class, 1L);

            ar.edu.unam.veterinaria.model.TipoServicio tsBd = obtenerOCrearTipoServicio(em, tipoServicio.getNombreDescriptivo());

            // ---> DEFENSA 1: Validar Cupos Diarios <---
            Long ocupacionActual = em.createQuery("SELECT COUNT(s) FROM Servicio s JOIN s.turno t WHERE s.tipoServicio.id = :tsId AND t.fecha = :fecha AND t.estado != 'CANCELADO'", Long.class)
                    .setParameter("tsId", tsBd.getId())
                    .setParameter("fecha", fecha)
                    .getSingleResult();
            tsBd.validarCupo(fecha, ocupacionActual.intValue()); // El dominio lanza CupoLLeno si no hay lugar

            ar.edu.unam.veterinaria.model.Peluqueria peluqueria = new ar.edu.unam.veterinaria.model.Peluqueria();
            peluqueria.setTipoServicio(tsBd);
            peluqueria.setTipoCorte(tipoServicio.getNombreDescriptivo());
            peluqueria.setObservaciones(observaciones);

            ar.edu.unam.veterinaria.model.Turno turno = new ar.edu.unam.veterinaria.model.Turno();
            turno.setCliente(cliente);
            turno.setMascota(mascota);
            turno.setVeterinario(veterinario);
            turno.setFecha(fecha);
            turno.setHora(hora);
            turno.setEstado(ar.edu.unam.veterinaria.model.EstadoTurno.PENDIENTE);
            turno.agregarServicio(peluqueria);

            em.persist(turno);
            em.getTransaction().commit();
        } catch (ar.edu.unam.veterinaria.exception.CupoLLeno e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally { em.close(); }
    }
}