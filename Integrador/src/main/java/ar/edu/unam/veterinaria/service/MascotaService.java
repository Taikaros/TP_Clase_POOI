package ar.edu.unam.veterinaria.service;

import ar.edu.unam.veterinaria.AppVeterinaria;
import ar.edu.unam.veterinaria.dto.MascotaDTO;
import ar.edu.unam.veterinaria.mapper.MascotaMapper;
import ar.edu.unam.veterinaria.model.Cliente;
import ar.edu.unam.veterinaria.model.Mascota;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.stream.Collectors;

public class MascotaService {

    public MascotaDTO guardarMascota(MascotaDTO mascotaDTO) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        
        try {
            em.getTransaction().begin();

            // 1. Buscamos al dueño en la base de datos usando el ID que vino de la pantalla
            Cliente dueno = em.find(Cliente.class, mascotaDTO.getIdCliente());
            
            if (dueno == null) {
                throw new IllegalArgumentException("Error: El cliente especificado no existe en la base de datos.");
            }

            // 2. Armamos la entidad Mascota y le asignamos su dueño real
            Mascota mascota = new Mascota();
            mascota.setNombre(mascotaDTO.getNombreMascota());
            mascota.setEspecie(mascotaDTO.getEspecie());
            mascota.setRaza(mascotaDTO.getRaza());
            mascota.setFechaNacimiento(mascotaDTO.getFechaNacimiento());
            mascota.setDueno(dueno); // ¡Acá se hace la magia de la relación @ManyToOne!

            // 3. Guardamos en Neon
            em.persist(mascota);

            em.getTransaction().commit();

            // 4. Devolvemos el DTO actualizado con el ID que generó la base de datos
            return MascotaMapper.toDTO(mascota);
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public List<MascotaDTO> obtenerTodas() {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        
        try {
            TypedQuery<Mascota> query = em.createQuery("SELECT m FROM Mascota m", Mascota.class);
            List<Mascota> mascotasBD = query.getResultList();

            return mascotasBD.stream()
                    .map(MascotaMapper::toDTO)
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }
}