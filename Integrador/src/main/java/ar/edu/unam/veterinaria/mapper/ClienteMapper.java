package ar.edu.unam.veterinaria.mapper;

import ar.edu.unam.veterinaria.dto.ClienteDTO;
import ar.edu.unam.veterinaria.model.Cliente;

public class ClienteMapper {
    public static ClienteDTO toDTO(Cliente cliente) {
        return new ClienteDTO(
            cliente.getId(), 
            cliente.getNombre(), 
            cliente.getApellido(), 
            cliente.getDni(),
            cliente.getTelefono(), 
            cliente.getEmail()
        );
    }

    public static Cliente toEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente(
            dto.getNombre(), 
            dto.getApellido(), 
            dto.getTelefono(), 
            dto.getEmail(),
            dto.getDni()
        );
        return cliente;
    }
}