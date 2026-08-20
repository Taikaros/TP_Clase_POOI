package TP_04.ejercicio01.src;

public class TestFiguras {
    public static void main(String[] args) {
        Figura[] figuras = new Figura[3];
        figuras[0] = new Rectangulo("Rectangulo", 4, 5);
        figuras[1] = new Circulo("Circulo", 3);
        figuras[2] = new Rectangulo("Rectangulo", 2, 3);
        
       
        double areaTotal = 0;

        for (Figura figura : figuras) {
            System.out.println(figura);
            
    
            areaTotal += figura.calcularArea();
            
            if (figura instanceof Dibujable) {
                ((Dibujable) figura).dibujar();
            }
            
            
            if (figura instanceof Coloreable) {
                ((Coloreable) figura).colorear("rojo");
            }
        }
        
        System.out.println("El área total es: " + areaTotal);
    }  
} 