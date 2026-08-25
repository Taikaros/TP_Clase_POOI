package ar.edu.unam.veterinaria.service;

import ar.edu.unam.veterinaria.AppVeterinaria;
import ar.edu.unam.veterinaria.model.Vacuna;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class VacunaService {

    public List<Vacuna> obtenerTodas() {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            TypedQuery<Vacuna> query = em.createQuery("SELECT v FROM Vacuna v", Vacuna.class);
            List<Vacuna> vacunas = query.getResultList();
            // Semilla inicial por si está vacía
            if (vacunas.isEmpty()) {
                em.getTransaction().begin();
                em.createNativeQuery("INSERT INTO vacunas_catalogo (nombreComercial, enfermedad, periodicidad) VALUES ('Defensor', 'Rabia', 12)").executeUpdate();
                em.createNativeQuery("INSERT INTO vacunas_catalogo (nombreComercial, enfermedad, periodicidad) VALUES ('Séxtuple', 'Moquillo/Parvovirus', 6)").executeUpdate();
                em.getTransaction().commit();
                vacunas = em.createQuery("SELECT v FROM Vacuna v", Vacuna.class).getResultList();
            }
            return vacunas;
        } finally {
            em.close();
        }
    }
    
    public Vacuna guardar(Vacuna vacuna) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            if (vacuna.getId() == null) {
                em.persist(vacuna); // Es nueva
            } else {
                vacuna = em.merge(vacuna); // Es actualización
            }
            em.getTransaction().commit();
            return vacuna;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    
    public void eliminar(Long id) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            Vacuna v = em.find(Vacuna.class, id);
            if (v != null) em.remove(v);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}