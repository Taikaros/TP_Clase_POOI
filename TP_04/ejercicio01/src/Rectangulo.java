package TP_04.ejercicio01.src;

public class Rectangulo extends Figura implements Dibujable{
    private double base, altura;

    public Rectangulo(String nombre, double base, double altura){
        super(nombre);
        this.base = base;
        this.altura = altura;
    }

    @Override
    public double calcularArea(){
        return base * altura;
    }

    @Override
    public void dibujar(){
        System.out.println("Dibujando rectangulo: " + nombre + "\n");
    }

    @Override 
    public String toString(){
        return super.toString() + ", base= " + base + ", altura= " + altura + ", area= " + calcularArea();
    }

}