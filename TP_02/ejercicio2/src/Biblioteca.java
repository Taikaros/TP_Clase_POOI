import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Biblioteca
{
    class Libro
    {
        private String isbn;
        private String titulo;
        private String autor;

        Set<String> categorias = new HashSet<>();

        Libro(String isbn, String titulo, String autor)
        {
            this.isbn = isbn;
            this.titulo = titulo;
            this.autor = autor;
        }

        public String getAutor() {
            return autor;
        }

        public String getIsbn() {
            return isbn;
        }

        public String getTitulo() {
            return titulo;
        }

        public Set<String> getCategorias() {
            return Collections.unmodifiableSet(categorias);
        }

        void agregarCategoria(String categoria)
        {
            categorias.add(categoria);
        }

        boolean perteneceACategoria(String categoria)
        {
            return categorias.contains(categoria);
        }

        @Override
        public String toString() {
            return "ISBN: " + isbn + ", Título: " + titulo + ", Autor: " + autor;
        }
    }

    //conjunto que almacena los ISBN de los libros prestados
    private Set<String> librosPrestados;

    //mapa que relaciona el ISBN del libro con el objeto Libro
    Map<String, Libro> librosPorIsbn = new HashMap<>();

    //mapa que relaciona la categoría con un conjunto de ISBN de libros que pertenecen a esa categoría
    Map<String, Set<String>> librosPorCategoria = new HashMap<>();

    Biblioteca()
    {
        librosPrestados = new HashSet<>();
    }

    public void agregarLibro(Libro libro)
    {
        //verifica que el libro no se encuentre ya en el mapa
        if(librosPorIsbn.containsKey(libro.getIsbn()))
        {
            throw new IllegalArgumentException("El libro ya existe en la biblioteca");
        }

        librosPorIsbn.put(libro.getIsbn(), libro);

        //agrega las categorias del libro al mapa de categorias
        for(String categoria : libro.getCategorias())
        {
            librosPorCategoria.putIfAbsent(categoria, new HashSet<>());

            librosPorCategoria.get(categoria).add(libro.getIsbn());
        }
    }

    public Libro buscarPorIsbn(String isbn)
    {
        //verifica que exista el libro en el mapa
        if(!librosPorIsbn.containsKey(isbn))
        {
            System.out.println("No se encontró el libro con ISBN: " + isbn);
        }

        return librosPorIsbn.get(isbn);
    }

    public Set<String> obtenerCategorias()
    {
        //devuelve un conjunto con las categorias existentes
        return new HashSet<>(librosPorCategoria.keySet());
    }

    public Set<Libro> buscarPorCategoria(String categoria)
    {
        //crea un conjunto para almacenar los libros encontrados
        Set<Libro> libros = new HashSet<>();

        //verifica que exista la categoria en el mapa
        if(!librosPorCategoria.containsKey(categoria))
        {
            System.out.println("No se encontraron libros en la categoría: " + categoria);

            return libros;
        }

        //busca los libros pertenecientes a la categoria
        for(String isbn : librosPorCategoria.get(categoria))
        {
            libros.add(librosPorIsbn.get(isbn));
        }

        return libros;
    }

    public boolean prestarLibro(String isbn)
    {
        //verifica que exista el libro
        if(!librosPorIsbn.containsKey(isbn))
        {
            return false;
        }

        //verifica que el libro no fue prestado
        if(librosPrestados.contains(isbn))
        {
            return false;
        }

        //agrega el libro a la lista de libros prestados
        librosPrestados.add(isbn);

        return true;
    }

    public boolean devolverLibro(String isbn)
    {
        //verifica que exista el libro
        if(!librosPorIsbn.containsKey(isbn))
        {
            return false;
        }

        //verifica que el libro haya sido prestado
        if(!librosPrestados.contains(isbn))
        {
            return false;
        }

        //elimina el libro de la lista de libros prestados
        librosPrestados.remove(isbn);

        return true;
    }

    public Set<Libro> obtenerLibrosDisponibles()
    {
        //crea un conjunto para almacenar los libros disponibles
        Set<Libro> librosDisponibles = new HashSet<>();

        //busca en el mapa de libros por ISBN y agrega al conjunto de libros disponibles aquellos que no estén prestados
        for(Libro libro : librosPorIsbn.values())
        {
            if(!librosPrestados.contains(libro.getIsbn()))
            {
                librosDisponibles.add(libro);
            }
        }

        return librosDisponibles;
    }

    void listarTodos()
    {
        for(Libro libro : librosPorIsbn.values())
        {
            if(librosPrestados.contains(libro.getIsbn()))
            {
                System.out.println(libro.toString() + " - Prestado");
            }
            else
            {
                System.out.println(libro.toString() + " - Disponible");
            }
        }
    }

    public static void main(String[] args) {

        Biblioteca biblioteca = new Biblioteca();

        // Crear libros
        Libro libro1 = biblioteca.new Libro(
            "111",
            "1984",
            "George Orwell"
        );

        Libro libro2 = biblioteca.new Libro(
            "222",
            "Dune",
            "Frank Herbert"
        );

        Libro libro3 = biblioteca.new Libro(
            "333",
            "Drácula",
            "Bram Stoker"
        );

        Libro libro4 = biblioteca.new Libro(
            "444",
            "Sherlock Holmes",
            "Arthur Conan Doyle"
        );

        // Agregar categorías
        libro1.agregarCategoria("Ciencia Ficción");
        libro1.agregarCategoria("Distopía");

        libro2.agregarCategoria("Ciencia Ficción");

        libro3.agregarCategoria("Horror");

        libro4.agregarCategoria("Misterio");

        // Registrar libros
        biblioteca.agregarLibro(libro1);
        biblioteca.agregarLibro(libro2);
        biblioteca.agregarLibro(libro3);
        biblioteca.agregarLibro(libro4);

        // Mostrar categorías
        System.out.println("Categorías:");
        System.out.println(
            biblioteca.obtenerCategorias()
        );

        // Buscar por categoría
        System.out.println("\nCiencia Ficción:");
        System.out.println(
            biblioteca.buscarPorCategoria(
                "Ciencia Ficción"
            )
        );

        // Categoría inexistente
        System.out.println("\nRomance:");
        System.out.println(
            biblioteca.buscarPorCategoria(
                "Romance"
            )
        );

        // Prestar libros
        System.out.println("\nPrestando libro 111:");
        System.out.println(
            biblioteca.prestarLibro("111")
        );

        System.out.println("\nIntentando prestar 111 otra vez:");
        System.out.println(
            biblioteca.prestarLibro("111")
        );

        // Todos los libros
        System.out.println("\nTodos los libros:");
        biblioteca.listarTodos();

        // Disponibles
        System.out.println("\nDisponibles:");
        System.out.println(
            biblioteca.obtenerLibrosDisponibles()
        );

        // Devolver libro
        System.out.println("\nDevolviendo 111:");
        System.out.println(
            biblioteca.devolverLibro("111")
        );

        // Verificar estado final
        System.out.println("\nEstado final:");
        biblioteca.listarTodos();
    }
}
