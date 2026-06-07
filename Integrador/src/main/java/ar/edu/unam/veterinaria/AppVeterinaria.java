package ar.edu.unam.veterinaria;

import javafx.application.Application;
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
    
    // Mantenemos la referencia no pública y exponemos un acceso controlado
    private static EntityManagerFactory emf;

    public static synchronized EntityManagerFactory getEmf() {
        return emf;
    }

    @Override
    public void start(Stage primaryStage) {
        // En tu próxima tarea (SCRUM-5), acá vas a cargar el MainLayout.fxml
        primaryStage.setTitle("Sistema de Gestión Veterinaria");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // 1. Cargar propiedades
        Properties dbProps = new Properties();
        try (InputStream input = AppVeterinaria.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                LOGGER.severe("No se pudo encontrar el archivo database.properties");
                return;
            }
            dbProps.load(input);
        } catch (Exception e) {
            LOGGER.severe("Error al cargar las propiedades de la base de datos: " + e.getMessage());
            return;
        }

        // 2. Inyectar contraseña y crear la conexión global
        Map<String, String> jpaProperties = new HashMap<>();
        jpaProperties.put("jakarta.persistence.jdbc.password", dbProps.getProperty("db.password"));
        
        try {
            emf = Persistence.createEntityManagerFactory("VeterinariaPU", jpaProperties);
            LOGGER.info("Conexión a la base de datos Neon establecida correctamente.");
            
            // 3. Arrancar la interfaz gráfica de JavaFX
            launch(args);
            
        } catch (Exception e) {
            LOGGER.severe("Error crítico al conectar con JPA: " + e.getMessage());
        } finally {
            // Nos aseguramos de cerrar la conexión a la BD solo cuando se cierra la aplicación
            if (emf != null && emf.isOpen()) {
                emf.close();
                LOGGER.info("Conexión a la base de datos cerrada.");
            }
        }
    }
}