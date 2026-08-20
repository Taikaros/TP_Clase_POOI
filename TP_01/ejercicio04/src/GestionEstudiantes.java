public class GestionEstudiantes
{
    // 🔹 Clase interna
    public class Estudiante
    {
        private String nombre;
        private int legajo;
        private double promedio;

        public Estudiante(String nombre, int legajo, double promedio)
        {
            this.nombre = nombre;
            this.legajo = legajo;
            this.promedio = promedio;
        }

        public String getNombre() { return nombre; }
        public int getLegajo() { return legajo; }
        public double getPromedio() { return promedio; }

        public boolean estaAprobado()
        {
            return promedio >= 6;
        }

        public String toString()
        {
            return "Nombre: " + nombre +
                   ", Legajo: " + legajo +
                   ", Promedio: " + promedio;
        }
    }

    // 🔹 ESTO VA AFUERA (arreglo clave)
    private Estudiante[] estudiantes;
    private int cantidad;

    public GestionEstudiantes()
    {
        estudiantes = new Estudiante[5];
        cantidad = 0;
    }

    boolean agregarEstudiante(String nombre, int legajo, double promedio)
    {
        if(cantidad < estudiantes.length)
        {
            estudiantes[cantidad] = new Estudiante(nombre, legajo, promedio);
            cantidad++;
            return true;
        }
        return false;
    }

    void listarEstudiantes()
    {
        for (int i = 0; i < cantidad; i++)
        {
            System.out.println(i + " - " + estudiantes[i]);
        }
    }

    public Estudiante buscarPorLegajo(int legajo)
    {
        for(int i = 0; i < cantidad; i++)
        {
            if(legajo == estudiantes[i].getLegajo())
            {
                return estudiantes[i];
            }
        }
        return null;
    }

    public double calcularPromedioGeneral()
    {
        if(cantidad == 0)
        {
            return 0;
        }

        double suma = 0;
        for(int i = 0; i < cantidad; i++)
        {
            suma += estudiantes[i].getPromedio();
        }
        return suma / cantidad;
    }

    public int obtenerAprobados()
    {
        int contador = 0;
        for(int i = 0; i < cantidad; i++)
        {
            if(estudiantes[i].estaAprobado())
            {
                contador++;
            }
        }
        return contador;
    }

    public Estudiante obtenerMejorEstudiante()
    {
        if(cantidad == 0)
        {
            return null;
        }

        Estudiante mejor = estudiantes[0];

        for (int i = 1; i < cantidad; i++)
        {
            if (estudiantes[i].getPromedio() > mejor.getPromedio())
            {
                mejor = estudiantes[i];
            }
        }
        return mejor;
    }

    public static void main(String[] args)
    {
        GestionEstudiantes lista = new GestionEstudiantes();

        lista.agregarEstudiante("Juan", 123, 7.5);
        lista.agregarEstudiante("Maria", 456, 8.0);
        lista.agregarEstudiante("Pedro", 789, 5.0);
        lista.agregarEstudiante("Ana", 4, 6.0);
        lista.agregarEstudiante("Jesus", 5, 4.5);

        boolean agregado = lista.agregarEstudiante("Extra", 6, 9.0);
        System.out.println("¿Se pudo agregar el sexto? " + agregado);

        System.out.println("\nLista de estudiantes:");
        lista.listarEstudiantes();

        Estudiante buscado = lista.buscarPorLegajo(456);
        if (buscado != null) {
            System.out.println("\nEstudiante encontrado: " + buscado);
        } else {
            System.out.println("\nEstudiante no encontrado.");
        }

        double promedio = lista.calcularPromedioGeneral();
        System.out.println("\nPromedio general: " + promedio);

        int aprobados = lista.obtenerAprobados();
        System.out.println("\nCantidad de aprobados: " + aprobados);

        Estudiante mejor = lista.obtenerMejorEstudiante();
        if (mejor != null) {
            System.out.println("\nMejor estudiante: " + mejor);
        } else {
            System.out.println("\nNo hay estudiantes registrados.");
        }

        System.out.println("\n¿El estudiante con legajo 456 está aprobado?");
        if (buscado != null)
        {
            System.out.println(buscado.estaAprobado());
        }
    }
}