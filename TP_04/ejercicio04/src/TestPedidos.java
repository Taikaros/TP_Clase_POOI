import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestPedidos {
    public static void main(String[] args) {
        
        List<Pedido> pedidos = new ArrayList<>();
        pedidos.add(new Pedido("Juan", LocalDate.of(2024, 6, 1), 150.0, EstadoPedido.CANCELADO));
        pedidos.add(new Pedido("Maria", LocalDate.of(2024, 6, 2), 200.0, EstadoPedido.PENDIENTE));
        pedidos.add(new Pedido("Carlos", LocalDate.of(2024, 6,  3), 300.0, EstadoPedido.ENVIADO));
        pedidos.add(new Pedido("Ana", LocalDate.of(2024, 6, 4), 250.0, EstadoPedido.PENDIENTE));
        pedidos.add(new Pedido("Luis", LocalDate.of(2024, 6, 5), 100.0, EstadoPedido.ENTREGADO));
        pedidos.add(new Pedido("Sofia", LocalDate.of(2024, 6, 6), 350.0, EstadoPedido.PENDIENTE));
        pedidos.add(new Pedido("Diego", LocalDate.of(2024, 6, 7), 400.0, EstadoPedido.ENVIADO));
        pedidos.add(new Pedido("Valentina", LocalDate.of(2024, 6, 8), 450.0, EstadoPedido.CANCELADO));

        List<Pedido> pedidos2 = new ArrayList<>();
        List<Pedido> pedidosRecientes = pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.PENDIENTE && p.getFecha().isAfter(LocalDate.now().minusDays(30)))
                .toList();
        
        System.out.println("Pedidos recientes pendientes:");
        pedidosRecientes.forEach(System.out::println);

        List<String> clientes = pedidos.stream()
                .map(Pedido::getCliente)
                .distinct()
                .sorted()   
                .toList();
        
        System.out.println("\nClientes únicos ordenados alfabéticamente:");
        clientes.forEach(System.out::println);

        Map<EstadoPedido, Double> sumaPorEstado = pedidos.stream()
                .collect(Collectors.groupingBy(Pedido::getEstado, Collectors.summingDouble(Pedido::getTotal)));
        
        System.out.println("\nSuma total por estado:");
        sumaPorEstado.forEach((estado, suma) -> System.out.println(estado + ": " + suma));

        Map<EstadoPedido, Double> promedioPorEstado = pedidos.stream()
                .collect(Collectors.groupingBy(Pedido::getEstado, Collectors.averagingDouble(Pedido::getTotal)));
        
        System.out.println("\nPromedio por estado:");
        promedioPorEstado.forEach((estado, promedio) -> System.out.println(estado + ": " + promedio));

        Optional<Pedido> pedidoMasCaro = pedidos2.stream()
                .max(Comparator.comparingDouble(Pedido::getTotal));
        
        System.out.println("\nBuscando el pedido más caro:");
        if (pedidoMasCaro.isPresent()) {
            System.out.println("Pedido más caro: " + pedidoMasCaro.get());
        } else {
            System.out.println("No se encontraron pedidos.");
        }

        String pedidosCancelados = pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.CANCELADO)
                .map(Pedido::getCliente)
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));
        
        System.out.println("\nClientes con pedidos cancelados: \n" + pedidosCancelados);
    }
}