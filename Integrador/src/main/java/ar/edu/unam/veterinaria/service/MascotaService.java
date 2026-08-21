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

    private void validarDatosMascota(MascotaDTO dto) {
        if (dto.getNombreMascota() == null || dto.getNombreMascota().trim().isEmpty()) throw new IllegalArgumentException("El nombre de la mascota es obligatorio.");
        if (dto.getEspecie() == null || dto.getEspecie().trim().isEmpty()) throw new IllegalArgumentException("Especie es obligatoria.");
        if (dto.getRaza() == null || dto.getRaza().trim().isEmpty()) throw new IllegalArgumentException("La raza es obligatoria.");
        if (dto.getFechaNacimiento() == null) throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
    }

    public MascotaDTO guardarMascota(MascotaDTO mascotaDTO) {
        validarDatosMascota(mascotaDTO);
        
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            Cliente dueno = em.find(Cliente.class, mascotaDTO.getIdCliente());
            if (dueno == null) throw new IllegalArgumentException("El cliente dueño no existe.");
            
            Mascota mascota = new Mascota();
            mascota.setNombreMascota(mascotaDTO.getNombreMascota());
            mascota.setEspecie(mascotaDTO.getEspecie());
            mascota.setRaza(mascotaDTO.getRaza());
            mascota.setFechaNacimiento(mascotaDTO.getFechaNacimiento());
            mascota.setNumeroFicha(mascotaDTO.getNumeroFicha() != null ? mascotaDTO.getNumeroFicha() : 0L);
            mascota.setDueno(dueno); 

            em.persist(mascota);
            em.getTransaction().commit();
            return MascotaMapper.toDTO(mascota);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException("Error interno al guardar mascota.");
        } finally {
            em.close();
        }
    }

    public List<MascotaDTO> obtenerTodas() {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            TypedQuery<Mascota> query = em.createQuery("SELECT m FROM Mascota m", Mascota.class);
            return query.getResultStream().map(MascotaMapper::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public MascotaDTO actualizarMascota(MascotaDTO mascotaDTO) {
        validarDatosMascota(mascotaDTO);
        
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            Mascota mascota = em.find(Mascota.class, mascotaDTO.getId());
            if (mascota != null) {
                mascota.setNombreMascota(mascotaDTO.getNombreMascota());
                mascota.setEspecie(mascotaDTO.getEspecie());
                mascota.setRaza(mascotaDTO.getRaza());
                mascota.setFechaNacimiento(mascotaDTO.getFechaNacimiento());
                mascota.setNumeroFicha(mascotaDTO.getNumeroFicha() != null ? mascotaDTO.getNumeroFicha() : 0L);
                em.merge(mascota);
            }
            em.getTransaction().commit();
            return MascotaMapper.toDTO(mascota);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Error al actualizar la mascota.");
        } finally {
            em.close();
        }
    }

    public void eliminarMascota(Long id) {
    EntityManager em = AppVeterinaria.getEmf().createEntityManager();
    try {
        em.getTransaction().begin();
        Mascota mascota = em.find(Mascota.class, id);
        
        if (mascota != null) {
            // SOLUCIÓN: Desvincular de la lista del dueño para que JPA/Cascade nos permita borrar
            Cliente dueno = mascota.getDueno();
            if (dueno != null) {
                dueno.getMascotas().remove(mascota);
            }
            em.remove(mascota);
        }
        
        em.getTransaction().commit();
    } catch (Exception e) {
        if (em.getTransaction().isActive()) em.getTransaction().rollback();
        e.printStackTrace(); // Imprime el error real en la consola por si falla
        throw new RuntimeException("Error al eliminar la mascota.");
    } finally {
        em.close();
    }
}
}