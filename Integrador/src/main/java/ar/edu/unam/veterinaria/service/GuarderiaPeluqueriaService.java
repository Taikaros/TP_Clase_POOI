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

    public void registrarGuarderia(Long idCliente, Long idMascota, LocalDate fecha, LocalTime hora, LocalDate fechaSalida, String jaula, String alimentacion, boolean actividad, String obs) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            Cliente c = em.find(Cliente.class, idCliente);
            Mascota m = em.find(Mascota.class, idMascota);
            Veterinario v = em.createQuery("SELECT v FROM Veterinario v", Veterinario.class).setMaxResults(1).getSingleResult();

            Turno t = new Turno();
            t.setCliente(c);
            t.setMascota(m);
            t.setVeterinario(v);
            t.setFecha(fecha);
            t.setHora(hora);
            t.setEstado(EstadoTurno.CONFIRMADO); 

            Guarderia g = new Guarderia();
            g.setJaulaAsignada(jaula);
            g.setObservaciones(obs);
            g.setFechaSalida(fechaSalida);
            g.setAlimentacionEspecifica(alimentacion);
            g.setRequiereActividad(actividad);
            g.setTipoServicio(obtenerOCrearTipoServicio(em, "Guardería"));

            t.agregarServicio(g);
            em.persist(t);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // Aquí aceptamos directamente el TipoServicio creado en BD
    public void registrarPeluqueria(Long idCliente, Long idMascota, LocalDate fecha, LocalTime hora, TipoServicio tipoServicio, String obs) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            Cliente c = em.find(Cliente.class, idCliente);
            Mascota m = em.find(Mascota.class, idMascota);
            Veterinario v = em.createQuery("SELECT v FROM Veterinario v", Veterinario.class).setMaxResults(1).getSingleResult();
            
            // Re-vincular el tipoServicio en este EntityManager
            TipoServicio tsBd = em.find(TipoServicio.class, tipoServicio.getId());

            Turno t = new Turno();
            t.setCliente(c);
            t.setMascota(m);
            t.setVeterinario(v);
            t.setFecha(fecha);
            t.setHora(hora);
            t.setEstado(EstadoTurno.PENDIENTE);

            Peluqueria p = new Peluqueria();
            p.setObservaciones(obs);
            p.setTipoServicio(tsBd); // Saca nombre descriptivo y precio base

            t.agregarServicio(p);
            em.persist(t);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}