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

    /**
     * Guarda un nuevo cliente en la base de datos.
     * Recibe un DTO de la pantalla y devuelve un DTO actualizado con el ID generado.
     */
    public ClienteDTO guardarCliente(ClienteDTO clienteDTO) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        
        try {
            em.getTransaction().begin();

            // 1. Traducir el DTO "liviano" a la Entidad "pesada" de JPA
            Cliente cliente = ClienteMapper.toEntity(clienteDTO);

            // 2. Guardar físicamente en PostgreSQL (Neon)
            em.persist(cliente);

            em.getTransaction().commit();

            // 3. Volver a traducir a DTO para devolverlo a la interfaz (ahora tiene su ID real)
            return ClienteMapper.toDTO(cliente);
            
        } catch (Exception e) {
            // Si algo explota (ej. error de red), deshacemos los cambios para no corromper la BD
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return null; 
        } finally {
            // SIEMPRE cerramos el EntityManager para liberar memoria
            em.close();
        }
    }

    /**
     * Obtiene todos los clientes de la base de datos.
     * Ideal para llenar el TableView principal.
     */
    public List<ClienteDTO> obtenerTodos() {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        
        try {
            // Hacemos la consulta JPQL apuntando a la Entidad
            TypedQuery<Cliente> query = em.createQuery("SELECT c FROM Cliente c", Cliente.class);
            List<Cliente> clientesBD = query.getResultList();

            // Usamos Streams para mapear toda la lista de Entidades a DTOs en una sola línea
            return clientesBD.stream()
                    .map(ClienteMapper::toDTO)
                    .collect(Collectors.toList());
                    
        } finally {
            em.close();
        }
    }
}