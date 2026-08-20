package ar.edu.unam.veterinaria.mapper;

import ar.edu.unam.veterinaria.dto.ClienteDTO;
import ar.edu.unam.veterinaria.model.Cliente;

public class ClienteMapper {
    public static ClienteDTO toDTO(Cliente cliente){
        if (cliente == null){
            return null;
        }
        return new ClienteDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getTelefono(),
                cliente.getEmail()
        );
    }
    public static Cliente toEntity(ClienteDTO dtoClienteDTO){
        if (dtoClienteDTO == null){
            return null;
        }
        Cliente cliente = new Cliente();

        //Metodos heredados de persona
        cliente.setDatosPersonales(dtoClienteDTO.getNombre(), dtoClienteDTO.getApellido());
        cliente.setContacto(dtoClienteDTO.getTelefono(), dtoClienteDTO.getEmail());
        
        return cliente;
    }
}
