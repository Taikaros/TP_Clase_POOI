package ar.edu.unam.veterinaria.mapper;

import ar.edu.unam.veterinaria.dto.TurnoDTO;
import ar.edu.unam.veterinaria.model.Turno;
import ar.edu.unam.veterinaria.model.Servicio;
import java.util.stream.Collectors;

public class TurnoMapper {
    public static TurnoDTO toDTO(Turno turno) {
        if (turno == null) return null;
        
        String detalles = turno.getServicios().stream()
            .map(Servicio::getDetallesServicio)
            .collect(Collectors.joining(" + "));
            
        if (detalles.isEmpty()) {
            detalles = "Pendiente de definición";
        }

        return new TurnoDTO(
            turno.getId(),
            turno.getFecha(),
            turno.getHora(),
            turno.getEstado().name(),
            turno.getMascota().getId(),
            turno.getMascota().getNombreMascota(),
            turno.getVeterinario().getId(),
            "Dr/a. " + turno.getVeterinario().getApellido(),
            turno.getCliente().getId(),
            turno.getCliente().getNombre() + " " + turno.getCliente().getApellido(),
            detalles, 
            null, 
            "",
            turno.calcularCostoTotal() // <-- Calcula la suma exacta
        );
    }
}