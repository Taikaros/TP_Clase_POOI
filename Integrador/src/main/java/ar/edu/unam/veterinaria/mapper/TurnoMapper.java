package ar.edu.unam.veterinaria.mapper;

import ar.edu.unam.veterinaria.dto.TurnoDTO;
import ar.edu.unam.veterinaria.model.Servicio;
import ar.edu.unam.veterinaria.model.Turno;
import ar.edu.unam.veterinaria.model.Vacunacion;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TurnoMapper {

    public static TurnoDTO toDTO(Turno turno) {
        // 1. Unir los nombres de los servicios para la tabla
        String detalles = turno.getServicios().stream()
                .map(Servicio::getDetallesServicio)
                .collect(Collectors.joining(" + "));
                
        if (detalles.isEmpty()) {
            detalles = "Pendiente de definición";
        }

        // 2. BUSCAR SI HAY UNA VACUNA EN EL HISTORIAL DE ESTE TURNO
        Long idVacuna = null;
        String nombreVacuna = null;
        
        for (Servicio servicio : turno.getServicios()) {
            if (servicio instanceof Vacunacion) {
                Vacunacion vac = (Vacunacion) servicio;
                if (vac.getVacunaAplicada() != null) {
                    idVacuna = vac.getVacunaAplicada().getId();
                    nombreVacuna = vac.getVacunaAplicada().getNombreComercial();
                }
            }
        }

        // 3. Devolver el DTO armado
        return new TurnoDTO(
                turno.getId(),
                turno.getFecha(), // Toma la fecha exacta en la que se agendó/realizó
                turno.getHora(),
                turno.getEstado().name(),
                turno.getMascota().getId(),
                turno.getMascota().getNombreMascota(),
                turno.getVeterinario().getId(),
                "Dr/a. " + turno.getVeterinario().getApellido(),
                turno.getCliente().getId(),
                turno.getCliente().getNombre() + " " + turno.getCliente().getApellido(),
                detalles,
                new ArrayList<>(), // Usado solo en UI
                "", // Notas
                turno.calcularCostoTotal(),
                idVacuna,
                nombreVacuna
        );
    }
}