package ar.edu.unam.veterinaria.service;

import ar.edu.unam.veterinaria.AppVeterinaria;
import ar.edu.unam.veterinaria.model.TipoServicio;
import jakarta.persistence.EntityManager;
import java.util.List;

public class TipoServicioService {
    
    public List<TipoServicio> obtenerTodos() {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            return em.createQuery("SELECT t FROM TipoServicio t ORDER BY t.nombreDescriptivo ASC", TipoServicio.class).getResultList();
        } finally {
            em.close();
        }
    }

    public TipoServicio guardar(TipoServicio ts) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(ts);
            em.getTransaction().commit();
            return ts;
        } finally {
            em.close();
        }
    }

    public TipoServicio actualizar(TipoServicio ts) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            TipoServicio actualizado = em.merge(ts);
            em.getTransaction().commit();
            return actualizado;
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            TipoServicio ts = em.find(TipoServicio.class, id);
            if (ts != null) {
                em.remove(ts);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}