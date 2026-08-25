package ar.edu.unam.veterinaria.service;

import ar.edu.unam.veterinaria.AppVeterinaria;
import ar.edu.unam.veterinaria.model.Vacuna;
import jakarta.persistence.EntityManager;
import java.util.List;

public class VacunaService {
    
    public List<Vacuna> obtenerTodas() {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            List<Vacuna> vacunas = em.createQuery("SELECT v FROM Vacuna v", Vacuna.class).getResultList();
            
            // Si la base de datos está vacía, creamos un par de vacunas de prueba automáticamente
            if (vacunas.isEmpty()) {
                em.getTransaction().begin();
                
                Vacuna antirrabica = new Vacuna();
                // Asumimos que le pusiste setters a tu modelo Vacuna. Si no, ajustalo a tu constructor.
                // antirrabica.setNombreComercial("Defensor (Antirrábica)"); 
                // antirrabica.setEnfermedad("Rabia");
                // antirrabica.setPeriodicidad(12); // 12 meses
                
                // Hacemos un SQL nativo rápido por si no tenés los setters armados
                em.createNativeQuery("INSERT INTO vacunas_catalogo (nombreComercial, enfermedad, periodicidad) VALUES ('Defensor', 'Rabia', 12)").executeUpdate();
                em.createNativeQuery("INSERT INTO vacunas_catalogo (nombreComercial, enfermedad, periodicidad) VALUES ('Sextuple', 'Moquillo/Parvovirus', 6)").executeUpdate();
                em.getTransaction().commit();
                
                vacunas = em.createQuery("SELECT v FROM Vacuna v", Vacuna.class).getResultList();
            }
            return vacunas;
        } finally {
            em.close();
        }
    }
}