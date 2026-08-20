package TP_03.ejercicio1.src;
import TP_03.ejercicio1.src.Vehiculo;

public class Moto extends Vehiculo{
    
    private int cilindrada;
    
    public Moto(String marca, String modelo, int año, int cilindrada)
    {
        super(marca, modelo, año);
        this.cilindrada = cilindrada;
    }
    public String toString()
    {
        return super.toString() + ", cilindrada=" + cilindrada + "]";
    }
}
