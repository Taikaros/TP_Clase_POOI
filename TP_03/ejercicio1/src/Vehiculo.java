package TP_03.ejercicio1.src;

public class Vehiculo {
    protected String marca;
    protected String modelo;
    protected int año;

    public Vehiculo(String marca, String modelo, int año) {
        this.marca = marca;
        this.modelo = modelo;
        this.año = año;
    }
    public String toString()
    {
        return "Vehiculo[marca=" + marca + ", modelo=" + modelo + ", año=" + año + "]";
    }
    public final String obtenerDescripcionCorta()
    {
        return marca + " " + modelo;
    }  
}