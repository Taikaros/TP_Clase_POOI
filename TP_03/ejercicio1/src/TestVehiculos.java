package TP_03.ejercicio1.src;
import TP_03.ejercicio1.src.Auto;
import TP_03.ejercicio1.src.Moto;
import TP_03.ejercicio1.src.Vehiculo;

public class TestVehiculos {
    
    public static void main(String[] args) 
    {
        Auto auto1 = new Auto("Ford", "Ranger", 2020, 4);
        Moto moto1 = new Moto("Honda", "CB500X", 2021, 500);

        System.out.println(auto1.toString());
        System.out.println(moto1.toString());

        System.out.println("Descripción corta del auto: " + auto1.obtenerDescripcionCorta());
        System.out.println("Descripción corta de la moto: " + moto1.obtenerDescripcionCorta());
    }
}
