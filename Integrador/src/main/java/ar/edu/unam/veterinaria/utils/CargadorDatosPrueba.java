package ar.edu.unam.veterinaria.utils;

import ar.edu.unam.veterinaria.AppVeterinaria;
import ar.edu.unam.veterinaria.model.Veterinario;
import ar.edu.unam.veterinaria.model.Especialidad;
import jakarta.persistence.EntityManager;

public class CargadorDatosPrueba {
    
    public static void inicializadorDatos(){
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try{
            em.getTransaction().begin();
            //Especialidades
            Especialidad cirugia = new Especialidad ("Cirugia General", "Especialidad que se encarga de realizar procedimientos quirúrgicos en animales.");
            Especialidad dermatologia = new Especialidad ("Dermatologia", "Especialidad que se encarga de tratar problemas de la piel y el pelaje de los animales.");

            //profesionales
            Veterinario drGomez = new Veterinario("Roberto", "Gomez", "3758-112233", "roberto@vet.com", "MAT-9988");
            //agregar especialidades
            drGomez.agregarEspecialidad(cirugia);
            drGomez.agregarEspecialidad(dermatologia);

            //Persistencia
            em.persist(drGomez);

            em.getTransaction().commit();
            System.out.println("Datos de prueba cargados correctamente.");
        }catch(Exception e){
            if(em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        }finally{
            em.close();
        }
    }
}
