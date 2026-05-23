package TP_03.ejercicio2.src;

public class TestEmpleado {
    public static void main (String[] args){
        Empleado[] misEmpleados = {
            new EmpleadoTiempoCompleto ("Ana", "32756097", 50000,12000),
            new EmpleadoTiempoCompleto  ("Pablo", "22123564", 60000,14000),
            new EmpleadoPorHora  ("Luis", "25793294", 40000,80000,5000),
            new EmpleadoPorHora  ("Juan", "42756097", 70000,14000,4000)

        };
    }

}
