package ar.edu.unam.veterinaria.service;

import ar.edu.unam.veterinaria.AppVeterinaria;
import ar.edu.unam.veterinaria.dto.ClienteDTO;
import ar.edu.unam.veterinaria.mapper.ClienteMapper;
import ar.edu.unam.veterinaria.model.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

public class ClienteService {

    private void validarDatosBase(ClienteDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) throw new IllegalArgumentException("El nombre es obligatorio.");
        if (dto.getApellido() == null || dto.getApellido().trim().isEmpty()) throw new IllegalArgumentException("El apellido es obligatorio.");
        if (dto.getDni() == null || dto.getDni().trim().isEmpty()) throw new IllegalArgumentException("El DNI es obligatorio.");
    }

    private void validarDuplicados(EntityManager em, ClienteDTO dto) {
        String jpql = "SELECT COUNT(c) FROM Cliente c WHERE c.dni = :dni AND c.id != :id AND c.activo = true";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("dni", dto.getDni())
                .setParameter("id", dto.getId() != 0 ? dto.getId() : -1L)
                .getSingleResult();

        if (count > 0) {
            throw new IllegalArgumentException("Ya existe un cliente activo registrado con el DNI especificado.");
        }
    }

    public ClienteDTO guardarCliente(ClienteDTO clienteDTO) {
        validarDatosBase(clienteDTO);
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            validarDuplicados(em, clienteDTO);
            Cliente cliente = ClienteMapper.toEntity(clienteDTO);
            em.persist(cliente);
            em.getTransaction().commit();
            return ClienteMapper.toDTO(cliente);
        } catch (IllegalArgumentException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException("Error interno en la base de datos.");
        } finally {
            em.close();
        }
    }

    public List<ClienteDTO> obtenerTodos() {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            String jpql = "SELECT DISTINCT c FROM Cliente c LEFT JOIN FETCH c.mascotas WHERE c.activo = true";
            TypedQuery<Cliente> query = em.createQuery(jpql, Cliente.class);
            return query.getResultStream().map(ClienteMapper::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public ClienteDTO actualizarCliente(ClienteDTO clienteDTO) {
        validarDatosBase(clienteDTO);
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            validarDuplicados(em, clienteDTO);
            Cliente cliente = em.find(Cliente.class, clienteDTO.getId());
            if (cliente != null) {
                cliente.setNombre(clienteDTO.getNombre());
                cliente.setApellido(clienteDTO.getApellido());
                cliente.setDni(clienteDTO.getDni());
                cliente.setTelefono(clienteDTO.getTelefono());
                cliente.setEmail(clienteDTO.getEmail());
                em.merge(cliente);
                em.getTransaction().commit();
                return ClienteMapper.toDTO(cliente);
            } else {
                throw new IllegalArgumentException("El cliente que intenta modificar ya no existe.");
            }
        } catch (IllegalArgumentException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar la base de datos.");
        } finally {
            em.close();
        }
    }

    public void darDeBaja(Long id) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            Cliente cliente = em.find(Cliente.class, id);
            if (cliente != null) {
                cliente.setActivo(false);
                em.merge(cliente);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException("Error al dar de baja.");
        } finally {
            em.close();
        }
    }
}