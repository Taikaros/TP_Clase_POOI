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

    public ClienteDTO guardarCliente(ClienteDTO clienteDTO) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            Cliente cliente = ClienteMapper.toEntity(clienteDTO);
            em.persist(cliente);
            em.getTransaction().commit();
            return ClienteMapper.toDTO(cliente);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return null; 
        } finally {
            em.close();
        }
    }

    public List<ClienteDTO> obtenerTodos() {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            TypedQuery<Cliente> query = em.createQuery("SELECT c FROM Cliente c WHERE c.activo = true", Cliente.class);
            List<Cliente> clientesBD = query.getResultList();
            return clientesBD.stream().map(ClienteMapper::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public ClienteDTO actualizarCliente(ClienteDTO clienteDTO) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            // Buscamos el cliente original
            Cliente cliente = em.find(Cliente.class, clienteDTO.getId());
            if (cliente != null) {
                // Actualizamos sus datos
                cliente.setDatosPersonales(clienteDTO.getNombre(), clienteDTO.getApellido());
                cliente.setContacto(clienteDTO.getTelefono(), clienteDTO.getEmail());
                em.merge(cliente); // merge = UPDATE en la base de datos
            }
            em.getTransaction().commit();
            return ClienteMapper.toDTO(cliente);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
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
            Cliente cliente = em.find(Cliente.class, id);
            if (cliente != null) {
                cliente.setActivo(false); // Baja lógica
                em.merge(cliente);
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