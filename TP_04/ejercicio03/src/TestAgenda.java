import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

public class TestAgenda {
    public static void main(String[] args) {
       
        Agento agenda = new Agento();
        

        Evento evento1 = new Evento("Cumpleaños Pasado", LocalDateTime.of(2024, 7, 15, 18, 0), "Casa de Juan");
        Evento evento2 = new Evento("Reunión", LocalDateTime.of(2026, 8, 20, 9, 30), "Oficina");
        Evento evento3 = new Evento("Concierto", LocalDateTime.of(2026, 11, 10, 21, 0), "Estadio");
        Evento evento4 = new Evento("Año Nuevo", LocalDateTime.of(2027, 1, 1, 0, 0), "Salón");
        
        agenda.agregarEvento(evento1);
        agenda.agregarEvento(evento2);
        agenda.agregarEvento(evento3);
        agenda.agregarEvento(evento4);
        
        System.out.println("--- BÚSQUEDA DE EVENTO EXISTENTE ---");
        
        agenda.buscarEvento("Concierto").ifPresent(evento -> {
            System.out.println("Impresión completa (toString): " + evento.toString());
            System.out.println("Fecha amigable: " + evento.formatearFecha());
        });
        
        System.out.println("\n--- BÚSQUEDA DE EVENTO INEXISTENTE ---");
        
        Evento eventoFalso = agenda.buscarEvento("Fiesta Secreta")
                .orElse(new Evento("Evento Default", LocalDateTime.now(), "Ninguna"));
        System.out.println("Al buscar 'Fiesta Secreta' devolvió: " + eventoFalso.nombre());
        
        System.out.println("\n--- EVENTOS PRÓXIMOS ---");
       
        List<Evento> proximos = agenda.eventosProximos(LocalDate.now());
        for (Evento e : proximos) {
            System.out.println("- " + e.nombre() + " el " + e.fecha().toLocalDate());
        }
        
        System.out.println("\n--- TIEMPO TRANSCURRIDO / RESTANTE ---");
     
        agenda.tiempoHastaEvento("Año Nuevo").ifPresent(System.out::println);
        agenda.tiempoHastaEvento("Cumpleaños Pasado").ifPresent(System.out::println);
        
        // demostracion de inmutabilidad en un record
        // evento1.fecha = LocalDateTime.now(); 
        // error: Los componentes de un record son finales, no se pueden modificar una vez creados
    }
}