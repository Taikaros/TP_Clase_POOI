import java.util.List;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Optional;

public class Agento {
    private List<Evento> eventos;
    public Agento() {
    }
    public void agregarEvento(Evento e){
        if (eventos == null) {
            eventos = new ArrayList<>();
        }
        eventos.add(e);
    }
    
    public Optional<Evento> buscarEvento(String nombre) {
        if (eventos != null) {
            for (Evento evento : eventos) {
                if (evento.nombre().equalsIgnoreCase(nombre)) {
                    return Optional.of(evento);
                }
            }
        }
        return Optional.empty();
    }

   public List<Evento> eventosProximos(LocalDate hoy) {
    if (eventos == null) return new ArrayList<>(); // Por si la lista está vacía
    
    return eventos.stream()
            // Filtramos quedándonos con las fechas que NO sean anteriores a hoy
            .filter(evento -> !evento.fecha().toLocalDate().isBefore(hoy))
            // Convertimos el stream resultante de nuevo a una lista
            .toList(); 
}

    public Optional<String> tiempoHastaEvento(String nombre) {
        Optional<Evento> eventoOpt = buscarEvento(nombre);
        if (eventoOpt.isPresent()) {
            Evento evento = eventoOpt.get();
            LocalDate hoy = LocalDate.now();
            if (evento.fecha().toLocalDate().isAfter(hoy)) {
                Period periodo = Period.between(hoy, evento.fecha().toLocalDate());
                return Optional.of("Faltan " + periodo.getYears() + " años, " + periodo.getMonths() + " meses y " + periodo.getDays() + " días.");
            } else {
               Period pasado = Period.between(evento.fecha().toLocalDate(), hoy);
               return Optional.of("Evento pasado hace " + pasado.getYears() + " años, " + pasado.getMonths() + " meses y " + pasado.getDays() + " días.");
            }
        }
        return Optional.empty();
    }

}
