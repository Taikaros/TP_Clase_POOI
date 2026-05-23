package TP_03.ejercicio3.src;

public class Perro extends Mamifero {
    private String raza;

    public Perro(String nombre, int mesesGestacion, String raza) {
        super(nombre, mesesGestacion); // Llama al constructor de Mamifero
        System.out.println("-> [3] Constructor Perro ejecutado");
        this.raza = raza;
    }

    @Override
    public String hacerSonido() {
        // Sobreescritura completa (no usamos super.hacerSonido())
        return "Guau! (sobreescribe completamente el sonido)";
    }

    @Override
    public String toString() {
        // Acumulación final
        return super.toString() + ", raza=" + raza;
    }
}
