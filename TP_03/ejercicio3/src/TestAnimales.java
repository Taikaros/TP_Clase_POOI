package TP_03.ejercicio3.src;

public class TestAnimales {
    public static void main(String[] args) {
        System.out.println("=== 1. CREACIÓN DEL OBJETO Y ORDEN DE CONSTRUCTORES ===");
        Perro miPerro = new Perro("Firulais", 2, "Labrador");

        System.out.println("\n=== 2. PRUEBA DE toString() ACUMULATIVO ===");
        System.out.println(miPerro.toString());

        System.out.println("\n=== 3. POLIMORFISMO Y hacerSonido() ===");
        // Referencias de distinto tipo apuntando al mismo objeto en memoria
        Animal refAnimal = miPerro;
        Mamifero refMamifero = miPerro;
        Perro refPerro = miPerro;

        System.out.println("Desde referencia Animal   -> " + refAnimal.hacerSonido());
        System.out.println("Desde referencia Mamifero -> " + refMamifero.hacerSonido());
        System.out.println("Desde referencia Perro    -> " + refPerro.hacerSonido());

        System.out.println("\n=== 4. COMPARACIÓN CON SUPERCLASES INSTANCIADAS ===");
        Animal unAnimal = new Animal("Bicho");
        System.out.println(unAnimal.toString() + " | Sonido: " + unAnimal.hacerSonido());
        
        Mamifero unMamifero = new Mamifero("Ballena", 11);
        System.out.println(unMamifero.toString() + " | Sonido: " + unMamifero.hacerSonido());
    }
}
