package ar.edu.unam.veterinaria.mapper;

import ar.edu.unam.veterinaria.dto.MascotaDTO;
import ar.edu.unam.veterinaria.model.Mascota;

public class MascotaMapper {
    
    public static MascotaDTO toDTO(Mascota mascota){
        if (mascota == null) {
            return null;
        }
        
        Long idCliente = 0L;
        String nombreDueno = "Sin Dueño";
        
        if (mascota.getDueno() != null) {
            idCliente = mascota.getDueno().getId();
            nombreDueno = mascota.getDueno().getNombre()+ " "+ mascota.getDueno().getApellido();
        }
        
        return new MascotaDTO(
            mascota.getId(), 
            mascota.getNombreMascota(), 
            mascota.getEspecie(), 
            mascota.getRaza(), 
            mascota.getFechaNacimiento(), 
            idCliente, 
            nombreDueno,
            mascota.getNumeroFicha() // Extraemos el numero de ficha
        );
    }
}