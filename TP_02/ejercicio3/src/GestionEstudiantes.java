import java.util.ArrayList;
import java.util.List;

public class GestionEstudiantes {
    private List<Estudiante> estudiantes;

    public GestionEstudiantes() {
        this.estudiantes = new ArrayList<>();
    }

    public void agregarEstudiante(Estudiante estudiante) {
        estudiantes.add(estudiante);
    }

    public void listarEstudiantes() {
        for (int i = 0; i < estudiantes.size(); i++) {
            System.out.println((i + 1) + ". " + estudiantes.get(i).toString());
        }
    }

    Estudiante buscarPorLegajo(int legajo) {
        for (int i = 0; i < estudiantes.size(); i++) {
            if (estudiantes.get(i).getLegajo() == legajo) {
                return estudiantes.get(i);
            }
        }
        return null;
    }
    
    public List<Estudiante> obtenerAprobados() {
        List<Estudiante> aprobados = new ArrayList<>();
        for (int i = 0; i < estudiantes.size(); i++) {
            if (estudiantes.get(i).estaAprobado()) {
                aprobados.add(estudiantes.get(i));
            }
        }
        return aprobados;
    }

    public Estudiante obtenerMejorEstudiante() {
        if (estudiantes.isEmpty()) {
            System.out.println("No hay estudiantes registrados.");
            return null;
        }
        Estudiante mejorEstudiante = estudiantes.get(0);
        for (int i = 1; i < estudiantes.size(); i++) {
            if (estudiantes.get(i).getPromedio() > mejorEstudiante.getPromedio()) {
                mejorEstudiante = estudiantes.get(i);
            }
        }
        System.out.println("El mejor estudiante es: " + mejorEstudiante.toString());
        return mejorEstudiante;
    }

    public double calcularPromedioGeneral() {
        if (estudiantes.isEmpty()) {
            System.out.println("No hay estudiantes registrados.");
            return 0.0;
        }
        double sumaPromedios = 0.0;
        for (int i = 0; i < estudiantes.size(); i++) {
            sumaPromedios += estudiantes.get(i).getPromedio();
        }
        double promedioGeneral = sumaPromedios / estudiantes.size();
        System.out.println("El promedio general de la clase es: " + promedioGeneral);
        return promedioGeneral;
    } 

 
     
public static void main(String[] args) {
    GestionEstudiantes gestion = new GestionEstudiantes();
    
    Estudiante estudiante1 = new Estudiante("Juan", 123, 7.5);
    Estudiante estudiante2 = new Estudiante("Maria", 456, 5.0);
    Estudiante estudiante3 = new Estudiante("Pedro", 789, 8.0);
    Estudiante estudiante4 = new Estudiante("Ana", 321, 6.0);
    Estudiante estudiante5 = new Estudiante("Luis", 654, 4.5);

    gestion.agregarEstudiante(estudiante1);
    gestion.agregarEstudiante(estudiante2);
    gestion.agregarEstudiante(estudiante3);
    gestion.agregarEstudiante(estudiante4);
    gestion.agregarEstudiante(estudiante5);

    System.out.println("Lista de estudiantes:");
    gestion.listarEstudiantes();
    
    System.out.println("\nBuscando estudiante con legajo 456:");
    Estudiante encontrado = gestion.buscarPorLegajo(456);
    if (encontrado != null) {
        System.out.println(encontrado.toString());
    } else {
        System.out.println("Estudiante no encontrado.");
    }
    
    System.out.println("\nEstudiantes aprobados:");
    List<Estudiante> aprobados = gestion.obtenerAprobados();
    for (Estudiante e : aprobados) {
        System.out.println(e.toString());
    }
    
    System.out.println("\nMejor estudiante:");
    gestion.obtenerMejorEstudiante();
    
    System.out.println("\nPromedio general:");
    gestion.calcularPromedioGeneral();
    }
}

 class Estudiante{
    private String nombre;
    private int legajo;
    private double promedio;
        
        public Estudiante(String nombre, int legajo, double promedio){
            this.nombre = nombre;
            this.legajo = legajo;
            this.promedio= promedio;
        }

        public boolean estaAprobado(){
            if (promedio >= 6.0){
                return true;
            } else {
                return false;
            }
        }

        public String getNombre(){
            return nombre;
        }
        public int getLegajo(){
            return legajo;
        }
        
        public double getPromedio(){
            return promedio;
        }
        @Override
        public String toString() {
            return "Estudiante{" +
                    "nombre='" + nombre + '\'' +
                    ", legajo=" + legajo +
                    ", promedio=" + promedio +
                    '}';
        }
    
        
    }  