package ar.edu.unam.veterinaria.mapper;

import ar.edu.unam.veterinaria.dto.VeterinarioDTO;
import ar.edu.unam.veterinaria.model.Veterinario;
import ar.edu.unam.veterinaria.model.Especialidad;
import java.util.List;
 

public class VeterinarioMapper {

    public static VeterinarioDTO toDTO(Veterinario vet) {
        if (vet == null) {
            return null;
        }

        // Convertimos la List<Especialidad> a List<String> extrayendo solo el nombre
        List<String> listaEspecialidades = vet.getEspecialidades().stream()
            .map(Especialidad::getNombreEspecialidad)
            .toList();

        return new VeterinarioDTO(
            vet.getId(),
            vet.getNombre(),
            vet.getApellido(),
            vet.getEspecialidad(),
            vet.getMatricula(),
            listaEspecialidades
        );
    }
}