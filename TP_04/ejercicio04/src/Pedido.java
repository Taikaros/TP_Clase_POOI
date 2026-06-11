import java.time.LocalDate;
public class Pedido {
    private String cliente;
    private LocalDate fecha;
    private double total;
    private EstadoPedido estado;

    public Pedido(String cliente, LocalDate fecha, double total, EstadoPedido estado) {
        this.cliente = cliente;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }
    @Override
    public String toString() {
        return "Pedido[" +
                "cliente='" + cliente + '\'' +
                ", fecha=" + fecha +
                ", total=" + total +
                ", estado=" + estado +
                ']';
    }
    public String getCliente() {
        return cliente;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public double getTotal() {
        return total;
    }
    public EstadoPedido getEstado() {
        return estado;
    }
    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

}
