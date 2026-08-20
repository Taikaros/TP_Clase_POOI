package ar.edu.unam.veterinaria.DAO;

import jakarta.persistence.EntityManager;
import java.util.List;
import ar.edu.unam.veterinaria.model.Cliente;
import ar.edu.unam.veterinaria.utils.JPAUtil; // Asegúrate de que esta ruta coincida con tu JPAUtil

public class ClienteDAO {

    public void insertar(Cliente cliente) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin(); 
            em.persist(cliente);  
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

    public void actualizar(Cliente cliente) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin(); 
            em.merge(cliente);        
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

    public List<Cliente> obtenerTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Cliente> clientes = em.createQuery("SELECT c FROM Cliente c WHERE c.activo = true", Cliente.class).getResultList();
        em.close();
        return clientes;
    }
}