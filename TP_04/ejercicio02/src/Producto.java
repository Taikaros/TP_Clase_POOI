public class Producto implements Comparable<Producto> {
    private String nombre;
    private double precio;
    private Categoria categoria;

    public Producto(String nombre, double precio, Categoria categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
    }
    public double calcularPrecioFinal() 
    {
        return precio - (precio * categoria.getDescuento() / 100);
    }
    @Override
    public int compareTo(Producto otroProducto) {
        return Double.compare(this.calcularPrecioFinal(), otroProducto.calcularPrecioFinal());
    }
    @Override
    public String toString() {
        return "Producto: " + nombre + ", Precio: " + precio + ", Categoria: " + categoria + ", Precio Final: " + calcularPrecioFinal();
    }
    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}