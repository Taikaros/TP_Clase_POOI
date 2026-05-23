package TP_03.ejercicio2.src;

public class EmpleadoPorHora extends Empleado {
    private int horasTrabajadas;
    private double valorHora;

    public EmpleadoPorHora(String nombre, String dni, double sueldoBase, int horasTrabajadas, double valorHora){
        super(nombre,dni,sueldoBase);
        this.horasTrabajadas = horasTrabajadas;
        this.valorHora = valorHora;
    }

    @Override
    public double calcularSalario(){
        return super.calcularSalario() + (horasTrabajadas * valorHora);
    }

    @Override 
    public String toString(){
        return super.toString() + ", horas= "+ horasTrabajadas + ", valorHora= " + valorHora + "]";
    } 

}
