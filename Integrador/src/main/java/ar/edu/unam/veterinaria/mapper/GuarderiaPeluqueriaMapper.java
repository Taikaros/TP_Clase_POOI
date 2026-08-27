package ar.edu.unam.veterinaria.mapper;

import ar.edu.unam.veterinaria.dto.GuarderiaDTO;
import ar.edu.unam.veterinaria.dto.PeluqueriaDTO;
import ar.edu.unam.veterinaria.model.Guarderia;
import ar.edu.unam.veterinaria.model.Peluqueria;
import ar.edu.unam.veterinaria.model.Turno;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class GuarderiaPeluqueriaMapper {

    public static GuarderiaDTO toGuarderiaDTO(Turno turno, Guarderia guarderia) {
        GuarderiaDTO dto = new GuarderiaDTO();
        dto.setIdTurno(turno.getId());
        dto.setIdServicio(guarderia.getId());
        dto.setMascotaNombre(turno.getMascota().getNombreMascota());
        dto.setClienteNombre(turno.getCliente().getNombre() + " " + turno.getCliente().getApellido());
        dto.setJaula(guarderia.getJaulaAsignada() != null ? guarderia.getJaulaAsignada() : "A asignar");
        
        dto.setFechaIngreso(turno.getFecha());
        dto.setHoraIngreso(turno.getHora()); 
        dto.setFechaSalida(guarderia.getFechaSalida()); 
        dto.setFechaNacimiento(turno.getMascota().getFechaNacimiento()); 
        
        if (guarderia.getFechaSalida() != null) {
            long diasTotal = ChronoUnit.DAYS.between(turno.getFecha(), guarderia.getFechaSalida());
            long diasParaCobro = diasTotal > 0 ? diasTotal : 1; // Si sale el mismo día, cobra 1 día min.
            
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dto.setTiempo(guarderia.getFechaSalida().format(fmt) + " (" + diasTotal + " días)");
            dto.setCostoTotal(diasParaCobro * 8000.0); // Precio fijo: 8 mil por día
        } else {
            dto.setTiempo("Sin salida asig.");
            dto.setCostoTotal(0.0);
        }
        
        dto.setAlimentacionEspecifica(guarderia.getAlimentacionEspecifica());
        dto.setRequiereActividad(guarderia.isRequiereActividad());
        
        dto.setEspecie(turno.getMascota().getEspecie());
        dto.setRaza(turno.getMascota().getRaza());
        dto.setObservaciones(guarderia.getObservaciones());
        dto.setNumeroFicha(turno.getMascota().getNumeroFicha());
        dto.setEstadoTurno(turno.getEstado().name());
        return dto;
    }

    public static PeluqueriaDTO toPeluqueriaDTO(Turno turno, Peluqueria peluqueria) {
        PeluqueriaDTO dto = new PeluqueriaDTO();
        dto.setIdTurno(turno.getId());
        dto.setIdServicio(peluqueria.getId());
        dto.setFecha(turno.getFecha());
        dto.setHora(turno.getHora());
        dto.setFechaNacimiento(turno.getMascota().getFechaNacimiento()); 
        dto.setMascotaNombre(turno.getMascota().getNombreMascota());
        dto.setClienteNombre(turno.getCliente().getNombre() + " " + turno.getCliente().getApellido());
        
        // El nombre descriptivo viene del TipoServicio y le sacamos el prefijo visual
        String nombreServicio = peluqueria.getTipoServicio() != null ? peluqueria.getTipoServicio().getNombreDescriptivo().replace("[PELUQUERÍA] ", "") : "Estética general";
        dto.setServicio(nombreServicio);
        dto.setCostoTotal(peluqueria.getPrecioHistorico() != null ? peluqueria.getPrecioHistorico() : 0.0);
        
        dto.setEstado(turno.getEstado().getDescripcion());
        dto.setEspecie(turno.getMascota().getEspecie());
        dto.setRaza(turno.getMascota().getRaza());
        dto.setObservaciones(peluqueria.getObservaciones());
        dto.setNumeroFicha(turno.getMascota().getNumeroFicha());
        return dto;
    }
}