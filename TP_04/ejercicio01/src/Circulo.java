package TP_04.ejercicio01.src;

public class Circulo extends Figura implements Dibujable, Coloreable{
    private double radio;

    public Circulo(String nombre, double radio){
        super(nombre);
        this.radio = radio;
    }

    @Override 
    public double calcularArea(){
        return Math.PI * radio * radio;
    }

    @Override
    public void dibujar(){
        System.out.println("Dibujando circulo: " + nombre);
    }

    @Override
    public void colorear(String color){
        System.out.println("Coloreando circulo de color: " + color + "\n");
    }

    @Override

    public String toString(){
        return super.toString() + ", radio= " + radio + ", area=" + calcularArea();
    }

    
}