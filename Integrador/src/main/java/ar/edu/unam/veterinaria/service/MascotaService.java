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
            Cliente dueno = em.find(Cliente.class, mascotaDTO.getIdCliente());
            if (dueno == null) throw new IllegalArgumentException("El cliente no existe.");
            
            Mascota mascota = new Mascota();
            mascota.setNombreMascota(mascotaDTO.getNombreMascota());
            mascota.setEspecie(mascotaDTO.getEspecie());
            mascota.setRaza(mascotaDTO.getRaza());
            mascota.setFechaNacimiento(mascotaDTO.getFechaNacimiento());
            mascota.setNumeroFicha(mascotaDTO.getNumeroFicha());
            mascota.setDueno(dueno); 
            
            em.persist(mascota);
            em.getTransaction().commit();
            return MascotaMapper.toDTO(mascota);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
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
            return mascotasBD.stream().map(MascotaMapper::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public MascotaDTO actualizarMascota(MascotaDTO mascotaDTO) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            Mascota mascota = em.find(Mascota.class, mascotaDTO.getId());
            if (mascota != null) {
                mascota.setNombreMascota(mascotaDTO.getNombreMascota());
                mascota.setEspecie(mascotaDTO.getEspecie());
                mascota.setRaza(mascotaDTO.getRaza());
                mascota.setFechaNacimiento(mascotaDTO.getFechaNacimiento());
                mascota.setNumeroFicha(mascotaDTO.getNumeroFicha());
                em.merge(mascota);
            }
            em.getTransaction().commit();
            return MascotaMapper.toDTO(mascota);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return null;
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
                em.remove(mascota); // Borrado físico de la BD
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}