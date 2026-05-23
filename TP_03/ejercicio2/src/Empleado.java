package TP_03.ejercicio2.src;

public class Empleado {
    private String nombre;
    private String dni;
    private double sueldoBase;
    
    public Empleado (String nombre, String dni, double sueldoBase){
       this.nombre = nombre;
       this.dni = dni;
       this.sueldoBase = sueldoBase;
    }

    final String getDni() {
        return dni;
    }

    public double calcularSalario() {
        return sueldoBase;  
    }

    public String toString(){
        return "Empleado: [nombre: , " + nombre + ", dni: " + dni + ", s    alario: " + sueldoBase + "]";   
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass()!= obj.getClass()){
            return false;
        }
        Empleado otroEmpleado = (Empleado) obj;

        return Objects.equals(this.dni, otroEmpleado.dni);
    }    

    @Override
    public int hashCode(){
        return Objects.hash(dni);
    }
    
}
