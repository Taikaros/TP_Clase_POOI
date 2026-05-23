package TP_03.ejercicio3.src;

public class Mamifero extends Animal {
    private int mesesGestacion;

    public Mamifero(String nombre, int mesesGestacion) {
        super(nombre); // Llama al constructor de Animal
        System.out.println("-> [2] Constructor Mamifero ejecutado");
        this.mesesGestacion = mesesGestacion;
    }

    @Override
    public String hacerSonido() {
        // Extensión del método original usando super
        return super.hacerSonido() + " (es un mamífero)";
    }

    @Override
    public String toString() {
        // Acumulación progresiva de información
        return super.toString() + ", gestacion=" + mesesGestacion + " meses";
    }
}
