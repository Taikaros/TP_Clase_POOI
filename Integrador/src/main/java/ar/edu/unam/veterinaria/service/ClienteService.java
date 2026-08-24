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
        if (dto.getTelefono() == null || dto.getTelefono().trim().isEmpty()) throw new IllegalArgumentException("El teléfono es obligatorio.");
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) throw new IllegalArgumentException("El email es obligatorio.");
    }

    private void validarDuplicados(EntityManager em, ClienteDTO dto) {
        // Busca si hay otro cliente con el mismo nombre y apellido, excluyendo el ID actual (útil para cuando modificamos)
        Long count = em.createQuery("SELECT COUNT(c) FROM Cliente c WHERE LOWER(c.nombre) = LOWER(:nombre) AND LOWER(c.apellido) = LOWER(:apellido) AND c.id != :id AND c.activo = true", Long.class)
                .setParameter("nombre", dto.getNombre().trim())
                .setParameter("apellido", dto.getApellido().trim())
                .setParameter("id", dto.getId())
                .getSingleResult();
        
        if (count > 0) {
            throw new IllegalArgumentException("Ya existe un cliente activo registrado con el nombre y apellido especificado.");
        }
    }

    public ClienteDTO guardarCliente(ClienteDTO clienteDTO) {
        validarDatosBase(clienteDTO);
        
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            validarDuplicados(em, clienteDTO); // Lanza error si está duplicado
            
            em.getTransaction().begin();
            Cliente cliente = ClienteMapper.toEntity(clienteDTO);
            em.persist(cliente);
            em.getTransaction().commit();
            return ClienteMapper.toDTO(cliente);
        } catch (IllegalArgumentException e) {
            throw e; // Repasamos la excepción de negocio tal cual
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
            // Traemos al cliente con su lista de mascotas pegada
            String jpql = "SELECT DISTINCT c FROM Cliente c " +
                          "LEFT JOIN FETCH c.mascotas " +
                          "WHERE c.activo = true";
                          
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
            validarDuplicados(em, clienteDTO);

            em.getTransaction().begin();
            Cliente cliente = em.find(Cliente.class, clienteDTO.getId());
            if (cliente != null) {
                cliente.setDatosPersonales(clienteDTO.getNombre(), clienteDTO.getApellido());
                cliente.setContacto(clienteDTO.getTelefono(), clienteDTO.getEmail());
                em.merge(cliente);
            } else {
                throw new IllegalArgumentException("El cliente que intenta modificar ya no existe.");
            }
            em.getTransaction().commit();
            return ClienteMapper.toDTO(cliente);
        } catch (IllegalArgumentException e) {
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
            throw new RuntimeException("Error al dar de baja.");
        } finally {
            em.close();
        }
    }
}