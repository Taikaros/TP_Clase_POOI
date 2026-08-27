package ar.edu.unam.veterinaria.service;

import ar.edu.unam.veterinaria.AppVeterinaria;
import ar.edu.unam.veterinaria.dto.EspecialidadDTO;
import ar.edu.unam.veterinaria.mapper.EspecialidadMapper;
import ar.edu.unam.veterinaria.model.Especialidad;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

public class EspecialidadService {

    public EspecialidadDTO guardarEspecialidad(EspecialidadDTO dto) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            Especialidad especialidad = EspecialidadMapper.toEntity(dto);
            em.persist(especialidad);
            em.getTransaction().commit();
            return EspecialidadMapper.toDTO(especialidad);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public List<EspecialidadDTO> obtenerTodas() {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            TypedQuery<Especialidad> query = em.createQuery("SELECT e FROM Especialidad e", Especialidad.class);
            return query.getResultList().stream().map(EspecialidadMapper::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public void eliminarEspecialidad(Long id) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            Especialidad esp = em.find(Especialidad.class, id);
            if (esp != null) em.remove(esp);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}