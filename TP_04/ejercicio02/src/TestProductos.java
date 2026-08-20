import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TestProductos {
    public static void main(String[] args) {
        
        List<Producto> productos = new ArrayList<>();
        productos.add(new Producto("Laptop", 1000000.0, Categoria.ELECTRONICA));
        productos.add(new Producto("Camisa", 50000.0, Categoria.ROPA));
        productos.add(new Producto("Pan", 1300.0, Categoria.ALIMENTOS));
        productos.add(new Producto("Celular", 800000.0, Categoria.ELECTRONICA));
        productos.add(new Producto("Pantalones", 70000.0, Categoria.ROPA));
        productos.add(new Producto("Leche", 1500.0, Categoria.ALIMENTOS));

        // Orden Natural
        Collections.sort(productos); 
        System.out.println("--- Productos ordenados por orden natural ---");
        productos.forEach(System.out::println);

        // Orden por nombre alfabético 
        productos.sort((p1, p2) -> p1.getNombre().compareTo(p2.getNombre()));
        System.out.println("\n--- Productos ordenados por nombre ---");
        productos.forEach(System.out::println);

        // Orden por categoría y precio final descendente 
        productos.sort(
            Comparator.comparing(Producto::getCategoria)
                    .thenComparing(Producto::calcularPrecioFinal)
                    .reversed() 
        );
        System.out.println("\n--- Productos ordenados por categoría y precio ---");
        productos.forEach(System.out::println);

        // Buscar el producto mas barato con Optional
        Optional<Producto> productoMasBarato = productos.stream()
            .min(Comparator.comparing(Producto::calcularPrecioFinal));
            
        System.out.println("\n--- Búsqueda del producto más barato ---");
        if (productoMasBarato.isPresent()) {
            System.out.println("Producto más barato: " + productoMasBarato.get());
        } else {
            System.out.println("No se encontraron productos.");
        }
    }
}