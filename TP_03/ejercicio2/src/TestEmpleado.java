package TP_03.ejercicio2.src;

public class TestEmpleado {
    public static void main (String[] args){
        Empleado[] misEmpleados = {
            new EmpleadoTiempoCompleto ("Ana", "32756097", 50000,12000),
            new EmpleadoTiempoCompleto  ("Pablo", "22123564", 60000,14000),
            new EmpleadoPorHora  ("Luis", "25793294", 40000,80000,5000),
            new EmpleadoPorHora  ("Juan", "42756097", 70000,14000,4000)

        };

        for (int i = 0; i < misEmpleados.length; i++){
            System.out.println("Empleado: " + misEmpleados[i].toString());
            System.out.println("Salario: " + misEmpleados[i].calcularSalario());
        }

       EmpleadoTiempoCompleto emp1 = new EmpleadoTiempoCompleto("Carlos", "12345678", 60000, 10000);
       EmpleadoTiempoCompleto emp2 = new EmpleadoTiempoCompleto("Jose", "12345678", 80000, 20000);
        
       System.out.println("¿Empleado 1 es igual a empleado 2? " + emp1.equals(emp2));

       EmpleadoPorHora emp3 = new EmpleadoPorHora("Maria", "12345678", 40000, 160, 500);

       System.out.println("¿El Empleado 1 es igual al Empleado 3? " + emp1.equals(emp3));
    }


}
