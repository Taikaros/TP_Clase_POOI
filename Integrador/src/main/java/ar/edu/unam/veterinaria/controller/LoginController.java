package ar.edu.unam.veterinaria.controller;

import ar.edu.unam.veterinaria.AppVeterinaria;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController {

    @FXML private VBox panelLogin;
    @FXML private VBox panelLoading;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;

    @FXML
    public void initialize() {
        new Thread(() -> {
            AppVeterinaria.conectarBaseDeDatos(); 
            Platform.runLater(() -> {
                panelLoading.setVisible(false);
                panelLogin.setDisable(false);
            });
        }).start();
    }

    @FXML
    public void ingresar(ActionEvent event) {
        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        if ("admin".equals(user) && "admin123".equals(pass)) {
            // Ocultamos el form y mostramos la carga para enmascarar el lag visual
            panelLogin.setDisable(true);
            panelLoading.setVisible(true);

            // Le damos 150ms a JavaFX para que alcance a dibujar el "spinner" dando vueltas
            PauseTransition pause = new PauseTransition(Duration.millis(150));
            pause.setOnFinished(e -> cargarPantallaPrincipal(event));
            pause.play();
            
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Autenticación");
            alert.setHeaderText(null);
            alert.setContentText("Usuario o contraseña incorrectos.");
            alert.showAndWait();
        }
    }

    private void cargarPantallaPrincipal(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainLayout.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1366, 768); 
            scene.getStylesheets().add(getClass().getResource("/views/style.css").toExternalForm());

            stage.setTitle("Huellas & Salud - Centro Veterinario");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}