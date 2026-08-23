package ar.edu.unam.veterinaria.service;

import ar.edu.unam.veterinaria.AppVeterinaria;
import ar.edu.unam.veterinaria.dto.VeterinarioDTO;
import ar.edu.unam.veterinaria.mapper.VeterinarioMapper;
import ar.edu.unam.veterinaria.model.Veterinario;
import ar.edu.unam.veterinaria.model.Especialidad;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.stream.Collectors;

public class VeterinarioService {

    public List<VeterinarioDTO> obtenerTodos() {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            // Solo traemos a los veterinarios activos
            TypedQuery<Veterinario> query = em.createQuery(
                "SELECT v FROM Veterinario v WHERE v.activo = true", 
                Veterinario.class
            );
            return query.getResultList().stream()
                        .map(VeterinarioMapper::toDTO)
                        .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public VeterinarioDTO guardarVeterinario(VeterinarioDTO dto, List<Long> idsEspecialidades) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            
            Veterinario vet = new Veterinario();
            
            // 1. Setear datos básicos heredados y propios
            vet.setDatosPersonales(dto.getNombre(), dto.getApellido());
            vet.setContacto(dto.getTelefono(), dto.getEmail());
            vet.setMatricula(dto.getMatricula());
            
            // 2. Setear la lista de horarios (ElementCollection)
            vet.setDiasDisponibles(dto.getDiasDisponibles());

            // 3. Vincular las especialidades (ManyToMany)
            if (idsEspecialidades != null && !idsEspecialidades.isEmpty()) {
                for (Long idEsp : idsEspecialidades) {
                    Especialidad esp = em.find(Especialidad.class, idEsp);
                    if (esp != null) {
                        vet.agregarEspecialidad(esp);
                    }
                }
            }

            em.persist(vet);
            em.getTransaction().commit();
            
            return VeterinarioMapper.toDTO(vet);
            
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

    public VeterinarioDTO actualizarVeterinario(VeterinarioDTO dto, List<Long> idsEspecialidades) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            
            // Buscamos al profesional existente
            Veterinario vet = em.find(Veterinario.class, dto.getId());
            
            if (vet != null) {
                vet.setDatosPersonales(dto.getNombre(), dto.getApellido());
                vet.setContacto(dto.getTelefono(), dto.getEmail());
                vet.setMatricula(dto.getMatricula());
                
                // Actualizamos horarios
                vet.setDiasDisponibles(dto.getDiasDisponibles());
                
                // Limpiamos las especialidades viejas para cargar la nueva selección
                vet.getEspecialidades().clear();
                
                if (idsEspecialidades != null && !idsEspecialidades.isEmpty()) {
                    for (Long idEsp : idsEspecialidades) {
                        Especialidad esp = em.find(Especialidad.class, idEsp);
                        if (esp != null) {
                            vet.agregarEspecialidad(esp);
                        }
                    }
                }
                
                em.merge(vet);
            }
            
            em.getTransaction().commit();
            return VeterinarioMapper.toDTO(vet);
            
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

    public void darDeBaja(Long id) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            
            Veterinario vet = em.find(Veterinario.class, id);
            if (vet != null) {
                // Baja lógica: oculta al profesional del directorio pero 
                // mantiene intacto el historial de los turnos que ya atendió
                vet.setActivo(false); 
                em.merge(vet);
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