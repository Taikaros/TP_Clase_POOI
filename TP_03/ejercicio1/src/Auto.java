package TP_03.ejercicio1.src;
import TP_03.ejercicio1.src.Vehiculo;

public class Auto extends Vehiculo {
    
    private int cantidadPuertas;
    /*@Override
    public final String obtenerDescripcionCorta()
    {
        return marca + " " + modelo + " " + cantidadPuertas;
    } */
    public Auto(String marca, String modelo, int año, int cantidadPuertas)
    {
        super(marca, modelo, año);
        this.cantidadPuertas = cantidadPuertas;
    }
    public String toString()
    {
        return super.toString() + ", puertas=" + cantidadPuertas + "]";
    }
}
