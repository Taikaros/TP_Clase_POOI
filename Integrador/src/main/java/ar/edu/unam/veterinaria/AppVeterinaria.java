package ar.edu.unam.veterinaria;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class AppVeterinaria extends Application {

    private static final Logger LOGGER = Logger.getLogger(AppVeterinaria.class.getName());
    
    // Mantenemos la referencia no pública y exponemos un acceso controlado para JPA
    private static EntityManagerFactory emf;

    public static synchronized EntityManagerFactory getEmf() {
        return emf;
    }

    // Se agrega 'throws Exception' porque FXMLLoader.load() puede lanzar una IOException
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        // 1. Obtenemos la URL del archivo de forma segura
        java.net.URL fxmlLocation = AppVeterinaria.class.getResource("/views/MainLayout.fxml");
        
        // 2. Validación crítica
        if (fxmlLocation == null) {
            System.err.println("¡ERROR CRÍTICO!: Java no encuentra el archivo FXML.");
            System.err.println("Verificá que el archivo exista exactamente en: src/main/resources/views/MainLayout.fxml");
            System.exit(1); // Cerramos el programa con error
        }

        // 3. Si lo encuentra, lo cargamos
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // 4. Creamos la escena y mostramos la ventana
        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setTitle("Huellas & Salud - Centro Veterinario");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        // Buena práctica: Cerrar el EntityManagerFactory cuando se cierra la aplicación de JavaFX
        if (emf != null && emf.isOpen()) {
            emf.close();
            LOGGER.info("EntityManagerFactory cerrado correctamente.");
        }
        super.stop();
    }

    public static void main(String[] args) {
        // Aquí podrías inicializar tu emf = Persistence.createEntityManagerFactory("tuUnidadDePersistencia");
        // antes de lanzar la interfaz gráfica, o hacerlo mediante un bloque static.
        launch(args);
    }
}