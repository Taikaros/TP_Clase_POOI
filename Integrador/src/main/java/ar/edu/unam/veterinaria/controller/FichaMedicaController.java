package ar.edu.unam.veterinaria.controller;

import ar.edu.unam.veterinaria.dto.MascotaDTO;
import ar.edu.unam.veterinaria.dto.TurnoDTO;
import ar.edu.unam.veterinaria.service.MascotaService;
import ar.edu.unam.veterinaria.service.TurnoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.paint.Color;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class FichaMedicaController {

    @FXML private TextField txtBuscarMascota;
    @FXML private ListView<MascotaDTO> listaPacientes;
    @FXML private VBox panelDetalle;
    @FXML private Label lblPerfilNombre;
    @FXML private Label lblPerfilFicha;
    @FXML private Label lblPerfilDetalles;
    @FXML private Label lblPerfilEdad;
    @FXML private Label lblPerfilRegistros;
    @FXML private VBox contenedorLineaTiempo;

    private MascotaService mascotaService = new MascotaService();
    private TurnoService turnoService = new TurnoService();
    private ObservableList<MascotaDTO> masterDataMascotas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        panelDetalle.setVisible(false);
        configurarListaPacientes();
        cargarPacientesDesdeBD();
        configurarBuscador();
    }

    private void cargarPacientesDesdeBD() {
        List<MascotaDTO> todasLasMascotas = mascotaService.obtenerTodas();
        if (todasLasMascotas != null) {
            masterDataMascotas.addAll(todasLasMascotas);
        }
    }

    private void configurarBuscador() {
        FilteredList<MascotaDTO> filteredData = new FilteredList<>(masterDataMascotas, p -> true);
        txtBuscarMascota.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(mascota -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (mascota.getNombreMascota().toLowerCase().contains(lowerCaseFilter)) return true;
                if (String.valueOf(mascota.getNumeroFicha()).contains(lowerCaseFilter)) return true;
                return false;
            });
        });
        listaPacientes.setItems(filteredData);
    }

    private void configurarListaPacientes() {
        listaPacientes.setCellFactory(param -> new ListCell<MascotaDTO>() {
            @Override
            protected void updateItem(MascotaDTO mascota, boolean empty) {
                super.updateItem(mascota, empty);
                if (empty || mascota == null) {
                    setText(null); setGraphic(null);
                } else {
                    HBox card = new HBox(15);
                    card.setAlignment(Pos.CENTER_LEFT);
                    card.getStyleClass().add("client-list-card"); // Usamos la clase CSS de las tarjetas

                    StackPane avatar = new StackPane();
                    avatar.setStyle("-fx-background-color: " + (isSelected() ? "#D1FAE5" : "#F8FAFC") + "; -fx-background-radius: 12; -fx-min-width: 50; -fx-min-height: 50;");
                    FontIcon icon = new FontIcon(mascota.getEspecie().toLowerCase().contains("gato") ? "fas-cat" : "fas-dog");
                    icon.setIconSize(24);
                    icon.setIconColor(Color.web(isSelected() ? "#059669" : "#D2B48C"));
                    avatar.getChildren().add(icon);

                    VBox info = new VBox(3);
                    Label lblNombre = new Label(mascota.getNombreMascota());
                    lblNombre.setStyle("-fx-font-weight: 900; -fx-font-size: 15px; -fx-text-fill: #1E293B;");
                    
                    String fichaStr = mascota.getNumeroFicha() != null && mascota.getNumeroFicha() > 0 
                             ? "FCH-" + String.format("%03d", mascota.getNumeroFicha()) : "Sin Ficha";
                    Label lblFicha = new Label(fichaStr);
                    lblFicha.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
                    
                    info.getChildren().addAll(lblNombre, lblFicha);
                    card.getChildren().addAll(avatar, info);
                    
                    if (isSelected()) {
                        card.setStyle("-fx-background-color: #F0FDF4; -fx-border-color: #2CA871; -fx-border-width: 1 4 1 1;");
                    }
                    
                    setGraphic(card);
                }
            }
        });
        
        listaPacientes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) mostrarDetallesPaciente(newVal);
        });
    }

    private void mostrarDetallesPaciente(MascotaDTO mascota) {
        panelDetalle.setVisible(true);
        lblPerfilNombre.setText(mascota.getNombreMascota());
        String fichaStr = mascota.getNumeroFicha() != null && mascota.getNumeroFicha() > 0 
                 ? "FCH-" + String.format("%03d", mascota.getNumeroFicha()) : "Sin Ficha";
        lblPerfilFicha.setText(fichaStr);
        lblPerfilDetalles.setText(mascota.getRaza() + " · " + mascota.getEspecie() + " · " + mascota.getNombreDueno());
        
        if (mascota.getFechaNacimiento() != null) {
            int edad = Period.between(mascota.getFechaNacimiento(), LocalDate.now()).getYears();
            lblPerfilEdad.setText("\uD83D\uDCC6 Edad: " + edad + " años"); // Emoji de calendario temporal
        } else {
            lblPerfilEdad.setText("\uD83D\uDCC6 Edad: Desconocida");
        }
        cargarLineaTiempo(mascota.getId());
    }

    private void cargarLineaTiempo(Long idMascota) {
        contenedorLineaTiempo.getChildren().clear();
        List<TurnoDTO> historial = turnoService.obtenerTodos().stream()
                .filter(t -> t.getIdMascota().equals(idMascota) && t.getEstado().equalsIgnoreCase("ATENDIDO"))
                .sorted((t1, t2) -> t2.getFecha().compareTo(t1.getFecha()))
                .collect(Collectors.toList());
                
        lblPerfilRegistros.setText("\uD83D\uDCCE Registros: " + historial.size() + " entradas");
        
        if (historial.isEmpty()) {
            Label vacio = new Label("No hay registros médicos para este paciente.");
            vacio.setStyle("-fx-text-fill: #94A3B8; -fx-font-style: italic; -fx-padding: 20;");
            contenedorLineaTiempo.getChildren().add(vacio);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        for (TurnoDTO turno : historial) {
            HBox tarjeta = new HBox(15);
            tarjeta.setPadding(new Insets(20));
            tarjeta.getStyleClass().add("card");
            
            boolean esVacuna = turno.getDetallesServicios() != null && turno.getDetallesServicios().contains("Vacunación");
            
            StackPane iconContainer = new StackPane();
            iconContainer.setPrefSize(50, 50);
            iconContainer.setStyle("-fx-background-radius: 8; " + (esVacuna ? "-fx-background-color: #FFF7ED;" : "-fx-background-color: #ECFDF5;"));
            
            FontIcon icon = new FontIcon(esVacuna ? "fas-syringe" : "fas-stethoscope");
            icon.setIconColor(Color.web(esVacuna ? "#F97316" : "#10B981"));
            icon.setIconSize(24);
            iconContainer.getChildren().add(icon);

            VBox contenido = new VBox(8);
            HBox.setHgrow(contenido, Priority.ALWAYS);
            
            HBox header = new HBox(12);
            header.setAlignment(Pos.CENTER_LEFT);
            Label lblTitulo = new Label(esVacuna ? "Aplicación de Vacuna" : "Consulta");
            lblTitulo.setStyle("-fx-font-weight: 900; -fx-font-size: 16px; -fx-text-fill: #1E293B;");
            
            Label lblPill = new Label(turno.getDetallesServicios() != null && !turno.getDetallesServicios().isEmpty() ? turno.getDetallesServicios() : "Atención General");
            lblPill.setStyle(esVacuna ? "-fx-background-color: #FFEDD5; -fx-text-fill: #C2410C; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;"
                                       : "-fx-background-color: #D1FAE5; -fx-text-fill: #047857; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");
            
            header.getChildren().addAll(lblTitulo, lblPill);
            
            HBox metaInfo = new HBox(20);
            Label lblFecha = new Label("\uD83D\uDCC5 " + turno.getFecha().format(formatter)); 
            Label lblVet = new Label("\uD83D\uDC68\u200D\u2695\uFE0F Dr/a. " + turno.getNombreVeterinario());
            lblFecha.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13px;");
            lblVet.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13px;");
            metaInfo.getChildren().addAll(lblFecha, lblVet);
            
            Label lblNotasClinicas = new Label(turno.getNotas() != null && !turno.getNotas().isEmpty() ? turno.getNotas() : "Diagnóstico guardado en el sistema.");
            lblNotasClinicas.setWrapText(true);
            lblNotasClinicas.setStyle("-fx-text-fill: #334155; -fx-font-size: 14px; -fx-padding: 10 0 0 0;");
            
            contenido.getChildren().addAll(header, metaInfo, lblNotasClinicas);
            
            tarjeta.setStyle("-fx-background-color: white; -fx-border-color: " + (esVacuna ? "#FDBA74;" : "#6EE7B7;") + " -fx-border-width: 0 0 0 5; -fx-background-radius: 8; -fx-border-radius: 8;");
            tarjeta.getChildren().addAll(iconContainer, contenido);
            contenedorLineaTiempo.getChildren().add(tarjeta);
        }
    }

    @FXML public void filtrarPorFecha() { mostrarAlerta("Próximamente", "El filtro por rango de fechas estará disponible en la próxima actualización."); }
    @FXML public void filtrarPorServicio() { mostrarAlerta("Próximamente", "El filtro por tipo de servicio estará disponible en la próxima actualización."); }
    @FXML public void exportarPDF() { mostrarAlerta("Próximamente", "El módulo de exportación a PDF se encuentra en desarrollo."); }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo); alert.setHeaderText(null); alert.setContentText(mensaje); alert.showAndWait();
    }
}
