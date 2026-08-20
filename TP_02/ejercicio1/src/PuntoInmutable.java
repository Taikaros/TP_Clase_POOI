package TP_02.ejercicio1.src;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.UnaryOperator;

public final class PuntoInmutable {
 
    private final double x;
    private final double y;

    public PuntoInmutable(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "PuntoInmutable{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public PuntoInmutable trasladar(double dx, double dy) {
        return new PuntoInmutable(x + dx, y + dy);
    }
    public PuntoInmutable escalar(double factor){
        return new PuntoInmutable(x * factor, y * factor);
    }
    public PuntoInmutable reflejarEjeX(){
        return new PuntoInmutable(x, -y);
    }
    public PuntoInmutable reflejarEjeY(){
        return new PuntoInmutable(-x, y);
    }
    public double distanciaAlOrigen(){
        return java.lang.Math.sqrt(x*x + y*y);
    }
    public double distanciaA(PuntoInmutable otro){
        double dx = x - otro.x;
        double dy = y - otro.y;
        return java.lang.Math.sqrt(dx*dx + dy*dy);
    }
    public PuntoInmutable imprimir(String mensaje) {
    System.out.println(mensaje + ": " + this.toString());
    return this;
    }
}

class MenuPrincipal {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Punto inicial sobre el que trabajaremos
        PuntoInmutable punto = new PuntoInmutable(3.0, 4.0);
        
        // Esta lista guardará la "receta" de funciones que el usuario elija
        List<UnaryOperator<PuntoInmutable>> cadenaDeFunciones = new ArrayList<>();

        while (true) {
            System.out.println("\n========================================");
            System.out.println(" PUNTO ACTUAL: " + punto);
            System.out.println(" OPERACIONES EN COLA: " + cadenaDeFunciones.size());
            System.out.println("========================================");
            System.out.println("1. Trasladar");
            System.out.println("2. Escalar");
            System.out.println("3. Reflejar en Eje X");
            System.out.println("4. Reflejar en Eje Y");
            System.out.println("5. Calcular distancia al origen (Inmediato)");
            System.out.println("6. Calcular distancia a otro punto (Inmediato)");
            System.out.println("7. EJECUTAR CADENA DE FUNCIONES ACUMULADAS");
            System.out.println("8. Limpiar cola / Reiniciar punto");
            System.out.println("9. Salir");
            System.out.print("Elija una opción: ");

            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1 -> {
                    System.out.print("Ingrese dx: ");
                    double dx = scanner.nextDouble();
                    System.out.print("Ingrese dy: ");
                    double dy = scanner.nextDouble();
                    // Guardamos la operación con sus datos
                    cadenaDeFunciones.add(p -> p.trasladar(dx, dy));
                    System.out.println(">>> Traslación añadida a la cola.");
                }
                case 2 -> {
                    System.out.print("Ingrese factor de escala: ");
                    double f = scanner.nextDouble();
                    cadenaDeFunciones.add(p -> p.escalar(f));
                    System.out.println(">>> Escala añadida a la cola.");
                }
                case 3 -> {
                    cadenaDeFunciones.add(p -> p.reflejarEjeX());
                    System.out.println(">>> Reflejo X añadido a la cola.");
                }
                case 4 -> {
                    cadenaDeFunciones.add(p -> p.reflejarEjeY());
                    System.out.println(">>> Reflejo Y añadido a la cola.");
                }
                case 5 -> {
                    System.out.println("Distancia al origen: " + punto.distanciaAlOrigen());
                }
                case 6 -> {
                    System.out.print("Ingrese X del otro punto: ");
                    double ox = scanner.nextDouble();
                    System.out.print("Ingrese Y del otro punto: ");
                    double oy = scanner.nextDouble();
                    System.out.println("Distancia: " + punto.distanciaA(new PuntoInmutable(ox, oy)));
                }
                case 7 -> {
                    if (cadenaDeFunciones.isEmpty()) {
                        System.out.println("No hay funciones en la cola para ejecutar.");
                    } else {
                        System.out.println("\n--- Ejecutando cadena correlacionada ---");
                        // Aplicamos cada función y mostramos el paso intermedio con tu método imprimir()
                        for (int i = 0; i < cadenaDeFunciones.size(); i++) {
                            punto = cadenaDeFunciones.get(i).apply(punto);
                            punto.imprimir("Paso " + (i + 1));
                        }
                        cadenaDeFunciones.clear(); // Vaciamos la cola tras ejecutar
                        System.out.println("--- Fin de la cadena ---");
                    }
                }
                case 8 -> {
                    cadenaDeFunciones.clear();
                    punto = new PuntoInmutable(3.0, 4.0);
                    System.out.println("Sistema reiniciado.");
                }
                case 9 -> {
                    System.out.println("Saliendo...");
                    return;
                }
                default -> System.out.println("Opción inválida.");
            }
        }
    }
}