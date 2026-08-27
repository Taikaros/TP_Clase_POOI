package ar.edu.unam.veterinaria.mapper;

import ar.edu.unam.veterinaria.dto.EspecialidadDTO;
import ar.edu.unam.veterinaria.model.Especialidad;

public class EspecialidadMapper {
    public static EspecialidadDTO toDTO(Especialidad especialidad) {
        if (especialidad == null) return null;
        return new EspecialidadDTO(
            especialidad.getId(),
            especialidad.getNombreEspecialidad(),
            especialidad.getDescripcion()
        );
    }

    public static Especialidad toEntity(EspecialidadDTO dto) {
        if (dto == null) return null;
        Especialidad esp = new Especialidad();
        esp.setNombreEspecialidad(dto.getNombreEspecialidad());
        esp.setDescripcion(dto.getDescripcion());
        return esp;
    }
}