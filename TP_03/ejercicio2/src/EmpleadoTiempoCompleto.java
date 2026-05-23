package TP_03.ejercicio2.src;

public class EmpleadoTiempoCompleto extends Empleado {
    private double bonoAnual;

    public EmpleadoTiempoCompleto(String nombre, String dni, double sueldoBase, double bonoAnual){
        super(nombre,dni,sueldoBase);
        this.bonoAnual = bonoAnual;
    }

    @Override
    public double calcularSalario() {
        return super.calcularSalario() + bonoAnual / 12;
    }

    @Override
    public String toString() {
        return super.toString() + ", bonoAnual: " + bonoAnual + "]";
}
