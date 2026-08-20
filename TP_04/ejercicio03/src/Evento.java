import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Evento(String nombre, java.time.LocalDateTime fecha, String ubicacion) {
    public String formatearFecha(){
        DateTimeFormatter fecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return this.fecha.format(fecha);
    }  
}
