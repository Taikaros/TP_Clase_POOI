public class Producto {

    private String codigo;
    private String nombre;
    private double precio = 0;
    private int stock = 0;

    public Producto(String codigo, String nombre, double precio, int stock) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;

        if (precio < 0 ) {
            this.precio = 0;
        }
        if (stock < 0) {
            this.stock = 0;
        }
    }

    public String getCodigo() {
        return codigo;
    }   
    public String getNombre() {
        return nombre;
    }
    public double getPrecio() {
        return precio;
    }
    public int getStock() {
        return stock;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public void setPrecio(double precio){
        if (precio >= 0 ) {
            this.precio = precio;
        }
    }

    public void setStock(int stock){
        if (stock >= 0) {
            this.stock = stock;
        }
    }

    public double calcularValorInventario() {
        return precio * stock;
    }

    public boolean descontarStock(int cantidad){
        if (this.stock >= cantidad){
            stock -= cantidad;
            return true;
        } else {
            return false;
        }
    }   

    public void aplicarDescuentos(double porcentaje){
        if (porcentaje >= 0 && porcentaje <= 100) {
            this.precio = this.precio - (this.precio * porcentaje / 100);
        }
    }

    public String toString() {
        return "Producto{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +  
                ", precio=" + precio +
                ", stock=" + stock +
                '}';
    }

    public static void main (String[] args){
        Producto producto = new Producto("001", "Laptop", 1000.0, 10);
        System.out.println(producto);
        Producto producto2 = new Producto("002", "Smartphone", -500.0, -5);
        System.out.println(producto2);

    }

}
