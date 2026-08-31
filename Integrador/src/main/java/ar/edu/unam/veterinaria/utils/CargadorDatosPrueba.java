package ar.edu.unam.veterinaria.utils;

import ar.edu.unam.veterinaria.AppVeterinaria;
import ar.edu.unam.veterinaria.model.Veterinario;
import ar.edu.unam.veterinaria.model.Especialidad;
import jakarta.persistence.EntityManager;

public class CargadorDatosPrueba {
    
    public static void inicializadorDatos() {
        jakarta.persistence.EntityManager em = ar.edu.unam.veterinaria.AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            
            // Verificamos si ya hay veterinarios antes de intentar guardar
            Long count = em.createQuery("SELECT COUNT(v) FROM Veterinario v", Long.class).getSingleResult();
            
            if (count == 0) {
                ar.edu.unam.veterinaria.model.Especialidad cirugia = new ar.edu.unam.veterinaria.model.Especialidad("Cirugia General", "Especialidad que se encarga de realizar procedimientos quirúrgicos en animales.");
                ar.edu.unam.veterinaria.model.Especialidad dermatologia = new ar.edu.unam.veterinaria.model.Especialidad("Dermatologia", "Especialidad que se encarga de tratar problemas de la piel y el pelaje de los animales.");
                ar.edu.unam.veterinaria.model.Veterinario drGomez = new ar.edu.unam.veterinaria.model.Veterinario("Roberto", "Gomez", "3758-112233", "roberto@vet.com", "MAT-9988");
                drGomez.agregarEspecialidad(cirugia);
                
                em.persist(cirugia);
                em.persist(dermatologia);
                em.persist(drGomez);
                System.out.println("Datos de prueba cargados correctamente.");
            }
            
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
