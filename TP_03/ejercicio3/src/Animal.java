package TP_03.ejercicio3.src;

public class Animal {
    private String nombre;

    public Animal(String nombre) {
        System.out.println("-> [1] Constructor Animal ejecutado");
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public String hacerSonido() {
        return "Sonido genérico de animal";
    }

    @Override
    public String toString() {
        return "Animal[nombre=" + nombre + "]";
    }
}
