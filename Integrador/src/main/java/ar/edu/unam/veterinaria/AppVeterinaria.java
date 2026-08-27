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
    private static EntityManagerFactory emf;

    public static synchronized EntityManagerFactory getEmf() { return emf; }

    @Override
    public void start(Stage primaryStage) throws Exception {
        java.net.URL fxmlLocation = AppVeterinaria.class.getResource("/views/login.fxml");
        if (fxmlLocation == null) {
            System.err.println("¡ERROR CRÍTICO!: Java no encuentra el archivo login.fxml.");
            System.exit(1);
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();
        Scene scene = new Scene(root, 1000, 600); 
        scene.getStylesheets().add(getClass().getResource("/views/style.css").toExternalForm());

        primaryStage.setTitle("Huellas & Salud - Inicio de Sesión");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void conectarBaseDeDatos() {
        if (emf != null && emf.isOpen()) return; 
        
        Properties dbProps = new Properties();
        try (InputStream input = AppVeterinaria.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) dbProps.load(input);
        } catch (Exception e) { LOGGER.severe("Error al cargar properties: " + e.getMessage()); }

        Map<String, String> jpaProperties = new HashMap<>();
        jpaProperties.put("jakarta.persistence.jdbc.password", dbProps.getProperty("db.password"));

        try {
            emf = Persistence.createEntityManagerFactory("VeterinariaPU", jpaProperties);
            LOGGER.info("Conexión a la base de datos Neon establecida correctamente.");
            ar.edu.unam.veterinaria.utils.CargadorDatosPrueba.inicializadorDatos();
            
            // ---> TRUCO DE OPTIMIZACIÓN: WARM-UP <---
            // Ejecutamos una consulta fantasma para obligar a Hibernate a calentar los motores
            try (jakarta.persistence.EntityManager em = emf.createEntityManager()) {
                em.createQuery("SELECT 1 FROM Cliente c").setMaxResults(1).getResultList();
            }
            
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
        launch(args);
    }
}