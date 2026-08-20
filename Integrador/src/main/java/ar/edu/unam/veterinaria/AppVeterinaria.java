package ar.edu.unam.veterinaria;

import javafx.application.Application;
import javafx.application.Platform;
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
    
    private static EntityManagerFactory emf;

    public static synchronized EntityManagerFactory getEmf() {
        return emf;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. CARGAMOS LA INTERFAZ GRÁFICA AL INSTANTE
        java.net.URL fxmlLocation = AppVeterinaria.class.getResource("/views/MainLayout.fxml");
        if (fxmlLocation == null) {
            System.err.println("¡ERROR CRÍTICO!: Java no encuentra el archivo FXML.");
            System.exit(1);
        }
        
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();
        Scene scene = new Scene(root, 1280, 720);
        
        // Ponemos un título temporal para darle un feedback al usuario de que está cargando
        primaryStage.setTitle("Huellas & Salud - Centro Veterinario (Conectando a la base de datos...)");
        primaryStage.setScene(scene);
        primaryStage.show();

        // 2. CONECTAMOS A NEON EN SEGUNDO PLANO (Background Thread)
        new Thread(() -> {
            conectarBaseDeDatos();
            
            // 3. UNA VEZ CONECTADO, VOLVEMOS AL HILO GRÁFICO PARA ACTUALIZAR LA VENTANA
            Platform.runLater(() -> {
                primaryStage.setTitle("Huellas & Salud - Centro Veterinario");
                LOGGER.info("¡Interfaz lista y base de datos operativa!");
            });
        }).start();
    }

    private void conectarBaseDeDatos() {
        Properties dbProps = new Properties();
        try (InputStream input = AppVeterinaria.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                dbProps.load(input);
            }
        } catch (Exception e) {
            LOGGER.severe("Error al cargar properties: " + e.getMessage());
        }

        Map<String, String> jpaProperties = new HashMap<>();
        jpaProperties.put("jakarta.persistence.jdbc.password", dbProps.getProperty("db.password"));

        try {
            emf = Persistence.createEntityManagerFactory("VeterinariaPU", jpaProperties);
            LOGGER.info("Conexión a la base de datos Neon establecida correctamente.");
            
            // Ejecutamos el script de datos de prueba
            ar.edu.unam.veterinaria.utils.CargadorDatosPrueba.inicializadorDatos();
            
        } catch (Exception e) {
            LOGGER.severe("Error crítico al conectar con JPA: " + e.getMessage());
        }
    }

    @Override
    public void stop() throws Exception {
        if (emf != null && emf.isOpen()) {
            emf.close();
            LOGGER.info("EntityManagerFactory cerrado correctamente.");
        }
        super.stop();
    }

    public static void main(String[] args) {
        // Ahora el main es súper liviano, solo "gatilla" la aplicación gráfica
        launch(args);
    }
}