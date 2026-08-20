package ar.edu.unam.veterinaria.DAO;

import jakarta.persistence.EntityManager;
import ar.edu.unam.veterinaria.model.Mascota;
import ar.edu.unam.veterinaria.utils.JPAUtil;

public class MascotaDAO {
    
    public void insertar(Mascota mascota) {
        EntityManager em = JPAUtil.getEntityManager();
    try {
        em.getTransaction().begin(); // 1. Inicia la transacción
        em.persist(mascota);         // 2. Ejecuta la acción
        em.getTransaction().commit(); // 3. Confirma los cambios
    } catch (Exception e) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback(); 
        }
        e.printStackTrace();
    } finally {
        em.close(); 
    }
    }

    public void actualizar(Mascota mascota) {
        EntityManager em = JPAUtil.getEntityManager();
    try {
        em.getTransaction().begin(); 
        em.merge(mascota);         
        em.getTransaction().commit(); 
    } catch (Exception e) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        e.printStackTrace();
    } finally {
        em.close(); 
    }
    }

    public void eliminarFisico(Mascota mascota) {
        EntityManager em = JPAUtil.getEntityManager();
    try {
        em.getTransaction().begin(); 
        Mascota m = em.merge(mascota); 
        em.remove(m); 
        em.getTransaction().commit(); 
    } catch (Exception e) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback(); 
        }
        e.printStackTrace();
    } finally {
        em.close(); 
    }
    }
}