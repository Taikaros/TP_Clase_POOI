package TP_04.ejercicio01.src;

public abstract class Figura {
        protected String nombre; 

        public Figura(String nombre){
            this.nombre = nombre;
        }

        public abstract double calcularArea();
        

        public String getNombre() {
            return nombre;
        }

        @Override
        public String toString(){
            return "Figura[nombre=" + nombre + "] ";
            }

        
}