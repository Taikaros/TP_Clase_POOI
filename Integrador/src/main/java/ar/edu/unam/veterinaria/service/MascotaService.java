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

    public MascotaDTO guardarMascota(MascotaDTO dto) {
        validarDatosMascota(dto);
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            
            Cliente dueno = em.find(Cliente.class, dto.getIdCliente());
            if (dueno == null) throw new IllegalArgumentException("El cliente dueño no existe.");
            
            Mascota mascota = new Mascota();
            mascota.setNombreMascota(dto.getNombreMascota());
            mascota.setEspecie(dto.getEspecie());
            mascota.setRaza(dto.getRaza());
            mascota.setFechaNacimiento(dto.getFechaNacimiento());
            mascota.setDueno(dueno);

            // ---> AUTO-GENERADOR DE NÚMERO DE HISTORIA CLÍNICA <---
            Long nroFicha = dto.getNumeroFicha();
            if (nroFicha == null || nroFicha == 0L) {
                // Si el usuario dejó el casillero en blanco, buscamos el número más alto en la base de datos
                Long maxFicha = em.createQuery("SELECT MAX(m.numeroFicha) FROM Mascota m", Long.class).getSingleResult();
                nroFicha = (maxFicha != null) ? maxFicha + 1L : 1L; // Si no hay mascotas, arranca en 1
            }
            mascota.setNumeroFicha(nroFicha);

            em.persist(mascota);
            em.getTransaction().commit();
            return MascotaMapper.toDTO(mascota);
            
        } catch (IllegalArgumentException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
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

    public MascotaDTO actualizarMascota(MascotaDTO dto) {
        validarDatosMascota(dto);
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            Mascota mascota = em.find(Mascota.class, dto.getId());
            if (mascota != null) {
                mascota.setNombreMascota(dto.getNombreMascota());
                mascota.setEspecie(dto.getEspecie());
                mascota.setRaza(dto.getRaza());
                mascota.setFechaNacimiento(dto.getFechaNacimiento());
                
                // Solo actualiza la ficha si el usuario escribió un número manualmente para corregirlo
                if (dto.getNumeroFicha() != null && dto.getNumeroFicha() > 0) {
                    mascota.setNumeroFicha(dto.getNumeroFicha());
                }
                
                em.merge(mascota);
                em.getTransaction().commit();
                return MascotaMapper.toDTO(mascota);
            } else {
                throw new IllegalArgumentException("Error al actualizar la mascota.");
            }
        } catch (IllegalArgumentException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException("Error interno.");
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
                mascota.getDueno().getMascotas().remove(mascota);
                em.remove(mascota);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar la mascota.");
        } finally {
            em.close();
        }
    }
}