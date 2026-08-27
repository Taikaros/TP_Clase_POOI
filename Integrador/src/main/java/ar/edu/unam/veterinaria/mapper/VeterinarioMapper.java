package ar.edu.unam.veterinaria.mapper;

import ar.edu.unam.veterinaria.dto.EspecialidadDTO;
import ar.edu.unam.veterinaria.dto.VeterinarioDTO;
import ar.edu.unam.veterinaria.model.Veterinario;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VeterinarioMapper {

    public static VeterinarioDTO toDTO(Veterinario vet) {
        if (vet == null) return null;

        // Esto fuerza a cargar la lista de especialidades
        List<EspecialidadDTO> listaEspecialidades = vet.getEspecialidades().stream()
            .map(EspecialidadMapper::toDTO)
            .collect(Collectors.toList());

        // EL ARREGLO ESTÁ ACÁ:
        // Forzamos a Hibernate a buscar los horarios a la base de datos creando 
        // una lista nativa nueva mientras la conexión sigue abierta.
        List<String> diasReales = new ArrayList<>(vet.getDiasDisponibles());

        return new VeterinarioDTO(
            vet.getId(),
            vet.getNombre(),
            vet.getApellido(),
            vet.getTelefono(),
            vet.getEmail(),
            vet.getMatricula(),
            listaEspecialidades,
            diasReales // Pasamos la lista pura, sin fantasmas
        );
    }
}