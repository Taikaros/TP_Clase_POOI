package ar.edu.unam.veterinaria.service;

import ar.edu.unam.veterinaria.AppVeterinaria;
import ar.edu.unam.veterinaria.dto.VeterinarioDTO;
import ar.edu.unam.veterinaria.mapper.VeterinarioMapper;
import ar.edu.unam.veterinaria.model.Veterinario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.stream.Collectors;

public class VeterinarioService {

    public List<VeterinarioDTO> obtenerTodos() {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        
        try {
            // Buscamos todos los veterinarios en la base de datos
            TypedQuery<Veterinario> query = em.createQuery("SELECT v FROM Veterinario v", Veterinario.class);
            List<Veterinario> veterinariosBD = query.getResultList();

            // Convertimos toda la lista de entidades a DTOs usando el Mapper que armaste
            return veterinariosBD.stream()
                    .map(VeterinarioMapper::toDTO)
                    .collect(Collectors.toList());
                    
        } finally {
            em.close();
        }
    }

    // El método guardarVeterinario seguiría la misma lógica que ClienteService,
    // pero por ahora obtenerTodos es el vital para la pantalla de turnos.
}