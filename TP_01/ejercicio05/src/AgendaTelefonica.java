//Comentarios
// ---Guia de formato de comentario---
//! Un Error o no Usar un metod
//? Comentario de pregunta
// * Resaltar algo importante
/** @param {Tipo:param} nombreParam parametros o estructura de una funcion */

//* Objetivos
//*  Crear una Clase compleja que combine arrays, encapsilamiento, logia de negocio(operaciones CRUD), validaciones y manejo de estado
//* Descripcion
//** Implementar una clase @AgendaTelefonica que permita gestionar contactos telefonicos con operaciones de alta, baja, modificacion y consulta 
/** @param AgendaTelefonica {class} AgendaTelefonica - clase principal para el desarrollo del prolema 
//* Requisitos
/** @param Contacto {Class Internal} Contacto - clase interna que gestiono los contactos
/** @param nombre {string} nombre - atributo privado de la clase Contacto 
*   @param telefono {string[10]} telefono - atributo interno de la calse Contacto 
*/
//* ---Metodos --- */
/**
 * @param Contacto(String nombre, String telefono) {Constructor} es un constructor
 * Getters:
 * @param getNombre() metodo para obtener el nombre
 * @param getTelefono() metodo para obtener el telefono 
 * @param setTelefono(String telefono) tiene que estas vacio(void)
 * @param toString() {string} representacion del contacto
 */
//* ---Validacion de Telefono--- */
/**
 *! Debe tener exactamente 10 caracteres y todos deben ser digitos(0-9)
 *  @param AgendaTelefonica:
 ** Constante
 *  @param CAPACIDAD_MAXIMA {Static final int} - Constate con valor 10
 *  * Atributos Privados
 *  @param contacto {Contacto[]} - array de la clase contacto
 *  @param cantidadContactos {int} - cantidad actual de contactos
 *  *Constructor
 *  TODO: Inicializa el array con capacidad para @CAPACIDAD_MAXIMA contactos
 *  TODO: Inicializar @cantidadContactos en 0
 * 
 * *---Operaciones CRUD y utilidades:
 * * 1 Crear (Agregar)
 *  @param agregarContacto(String nombre, String telefono) {boolean}
 * TODO: Verifica que haya espacio disponible
 * TODO: Verificar que no exista un contacto con el mismo nombre (No es case sensitive);
 * TODO: Verifica que el Telefono tenga 10 digitos
 * TODO: Si todo es valido,agregar el contacto y retorna @true 
 * 
 ** 2 Leer (Buscar)
 * @param buscarPorNombre(String Nombre) {Contacto} - busca por nombre exacto (sin distinguir  entre mayúsculas/minúsculas). Retorna el contacto o @null
 * @param buscarPorTelefono(String telefono)  {Contacto} - Busca por numero de telefono exacto
 * @param buscarPorNombreParcial(String-prefijo) {String[]} - Retorna un array con los nombres de todos los contactos que contengan el prefijo. El array debe tener el tamaño exacto de las coincidencias
 * 
 * * 3 Actualizar
 * @param actualizarTelefono(String nombre, String nuevoTelefono) {boolean} - busca el contacto por nombre. Si existe y el nuevo telefono es valido, lo actualiza y retorna 
 * @return true
 * 
 * * 4 Eliminar
 * @param eliminarContacto(String nombre) {boolean}- Buscar el contacto por nombre. si existe, lo elimina y reordena el array para que no quede huecos(compactacion).
 * @return ture si se elimino
 * 
 * * 5 Utilidades 
 * @param listaContactos() {void} - Muestra todos los contactos 
 * @param estaLlena() {boolean}
 * @return true si la agenda esta llena
 * @param obtenerCantidad() {int} - retorna cuantos contactos hay
 * @param obtenerEspaciosDisponibles() {int} - retorna cuantos espacios quedan libres
 * 
 * 
 * *--- Metodo main
 *  *realizas un flujo completo de pruebas 
 * TODO: 1 Agregar 3 contactos validos con diferentes nombres y telefonos
 * TODO: 2 Intentar agregar un contacto con telefono invalido (debe fallar)
 * TODO: 3 Intentar agregar un contacto con nombre duplicado (debe fallar, probar mayuscula/minusculas)
 * TODO: 4 Listar todos los contactos
 * TODO: 5 Buscar por nombr( uno que existe, variando mayúsculas/minúsculas)
 * TODO: 6 Buscar por telefono
 * TODO: 7 Actualizar el telefono de un contacto existente
 * TODO: 8 Intentar actualizar con telefono invalido (debe fallar)
 * TODO: 9 Buscar por nombre parcial (ej: buscar "ar" debe encontrar "Maria", "Carlos",etc)
 * TODO: 10 Mostrar estadisticas: cantidad de contactos, espacios disponibles, si esta llena
 * TODO: 11 Eliminar un contacto (usar nombre con mayúsculas diferentes)
 * TODO: 12 Listar contactos nuevamente y verificar que se reordenó correctamente 
 * TODO: 13 Agregar contactos hasta llenar la agenda
 * TODO: 14 Verificar que esta llena e intentar agregar otro (debe fallar)
 * 
 * *---Conceptos a aplicar---
 * Composicion(clase dentro de clase)
 * Encapsulamiento estricto
 * Arrays con capacidad fija
 * Validaciones multiples
 * Comparacion de string sin distinguir mayusculas/minusculas
 * Reprdenamientos de arrays (compactacion tras eliminacion)
 * Creacion dinamica de arrays de resultado
 * Gestion de estado compleja
 * */

public class AgendaTelefonica {
    
    // --- Constante ---
    private static final int CAPACIDAD_MAXIMA = 10; [cite: 204]

    // --- Atributos privados ---
    private Contacto[] contactos; [cite: 206]
    private int cantidadContactos; [cite: 207]

    // --- Constructor ---
    public AgendaTelefonica() {
        this.contactos = new Contacto[CAPACIDAD_MAXIMA]; [cite: 210]
        this.cantidadContactos = 0; [cite: 211]
    }

    // --- Clase interna Contacto --- [cite: 191]
    private class Contacto {
        private String nombre; [cite: 193]
        private String telefono; [cite: 194]

        public Contacto(String nombre, String telefono) {
            this.nombre = nombre;
            this.telefono = telefono;
        }

        public String getNombre() { return nombre; }
        public String getTelefono() { return telefono; }

        public void setTelefono(String telefono) {
            // Solo actualiza si cumple la validación [cite: 199]
            if (validarTelefono(telefono)) {
                this.telefono = telefono;
            }
        }

        @Override
        public String toString() {
            return "Nombre: " + nombre + " | Teléfono: " + telefono; [cite: 199]
        }
    }

    // --- Validaciones y Utilidades ---
    
    private boolean validarTelefono(String telefono) {
        // Debe tener 10 caracteres y ser todos dígitos [cite: 201]
        return telefono != null && telefono.length() == 10 && telefono.matches("\\d+");
    }

    public boolean estaLlena() {
        return cantidadContactos == CAPACIDAD_MAXIMA; [cite: 238]
    }

    public int obtenerCantidad() {
        return cantidadContactos; [cite: 240]
    }

    public int obtenerEspaciosDisponibles() {
        return CAPACIDAD_MAXIMA - cantidadContactos; [cite: 242]
    }

    // --- Operaciones CRUD ---

    // 1. CREAR (Agregar)
    public boolean agregarContacto(String nombre, String telefono) {
        // Validar espacio, existencia previa y formato de teléfono [cite: 215, 216, 217]
        if (estaLlena()) return false;
        if (buscarPorNombre(nombre) != null) return false;
        if (!validarTelefono(telefono)) return false;

        contactos[cantidadContactos] = new Contacto(nombre, telefono);
        cantidadContactos++;
        return true; [cite: 218]
    }

    // 2. LEER (Buscar)
    public Contacto buscarPorNombre(String nombre) {
        for (int i = 0; i < cantidadContactos; i++) {
            // Comparación sin distinguir mayúsculas/minúsculas [cite: 222]
            if (contactos[i].getNombre().equalsIgnoreCase(nombre)) {
                return contactos[i];
            }
        }
        return null; [cite: 223]
    }

    public Contacto buscarPorTelefono(String telefono) {
        for (int i = 0; i < cantidadContactos; i++) {
            if (contactos[i].getTelefono().equals(telefono)) {
                return contactos[i];
            }
        }
        return null; [cite: 224]
    }

    public String[] buscarPorNombreParcial(String prefijo) {
        // Contar coincidencias para el tamaño del array [cite: 226]
        int contador = 0;
        for (int i = 0; i < cantidadContactos; i++) {
            if (contactos[i].getNombre().toLowerCase().contains(prefijo.toLowerCase())) {
                contador++;
            }
        }

        String[] resultados = new String[contador];
        int idx = 0;
        for (int i = 0; i < cantidadContactos; i++) {
            if (contactos[i].getNombre().toLowerCase().contains(prefijo.toLowerCase())) {
                resultados[idx++] = contactos[i].getNombre();
            }
        }
        return resultados; [cite: 226]
    }

    // 3. ACTUALIZAR
    public boolean actualizarTelefono(String nombre, String nuevoTelefono) {
        Contacto c = buscarPorNombre(nombre); [cite: 229]
        if (c != null && validarTelefono(nuevoTelefono)) { [cite: 230]
            c.setTelefono(nuevoTelefono);
            return true;
        }
        return false;
    }

    // 4. ELIMINAR (con compactación)
    public boolean eliminarContacto(String nombre) {
        for (int i = 0; i < cantidadContactos; i++) {
            if (contactos[i].getNombre().equalsIgnoreCase(nombre)) { [cite: 233]
                // Reordenar para no dejar huecos [cite: 234]
                for (int j = i; j < cantidadContactos - 1; j++) {
                    contactos[j] = contactos[j + 1];
                }
                contactos[cantidadContactos - 1] = null;
                cantidadContactos--;
                return true;
            }
        }
        return false;
    }

    public void listarContactos() {
        if (cantidadContactos == 0) {
            System.out.println("La agenda está vacía.");
        } else {
            for (int i = 0; i < cantidadContactos; i++) {
                System.out.println(contactos[i]); [cite: 236]
            }
        }
    }

    // --- Método MAIN para pruebas --- [cite: 243]
    public static void main(String[] args) {
        AgendaTelefonica agenda = new AgendaTelefonica();

        System.out.println("--- PRUEBAS DE AGENDA ---");

        // 1. Agregar contactos válidos [cite: 245]
        agenda.agregarContacto("Maria Lopez", "1122334455");
        agenda.agregarContacto("Carlos Gomez", "2233445566");
        agenda.agregarContacto("Juan Perez", "3344556677");

        // 2. Intento de teléfono inválido [cite: 246]
        System.out.println("Agregar 'Test' (9 dígitos): " + agenda.agregarContacto("Test", "123456789"));

        // 3. Intento de nombre duplicado [cite: 247]
        System.out.println("Agregar 'MARIA LOPEZ' (duplicado): " + agenda.agregarContacto("MARIA LOPEZ", "9999999999"));

        // 4. Listar [cite: 249]
        System.out.println("\nLista actual:");
        agenda.listarContactos();

        // 5. Buscar por nombre [cite: 250]
        System.out.println("\nBuscando 'carlos gomez': " + agenda.buscarPorNombre("carlos gomez"));

        // 7. Actualizar teléfono [cite: 252]
        agenda.actualizarTelefono("Juan Perez", "1010101010");
        System.out.println("Juan Perez actualizado: " + agenda.buscarPorNombre("Juan Perez"));

        // 9. Búsqueda parcial [cite: 254]
        System.out.println("\nBúsqueda parcial con 'ar':");
        for (String n : agenda.buscarPorNombreParcial("ar")) {
            System.out.println("- " + n);
        }

        // 10. Estadísticas [cite: 255]
        System.out.println("\nEstadísticas:");
        System.out.println("Cantidad: " + agenda.obtenerCantidad());
        System.out.println("Espacios libres: " + agenda.obtenerEspaciosDisponibles());

        // 11 y 12. Eliminar y listar [cite: 256, 257]
        System.out.println("\nEliminando a 'CARLOS GOMEZ'...");
        agenda.eliminarContacto("CARLOS GOMEZ");
        agenda.listarContactos();
        
        // 13 y 14. Llenar la agenda [cite: 258, 259]
        System.out.println("\nLlenando agenda...");
        for (int i = 0; i < 9; i++) {
            agenda.agregarContacto("Contacto" + i, "000000000" + i);
        }
        System.out.println("¿Está llena?: " + agenda.estaLlena());
        System.out.println("Intento agregar uno extra: " + agenda.agregarContacto("Extra", "1234567890"));
    }
}