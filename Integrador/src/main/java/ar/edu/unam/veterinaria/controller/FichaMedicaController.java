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
                    setText(null); setGraphic(null); setStyle("-fx-background-color: transparent;");
                } else {
                    HBox card = new HBox(15);
                    card.setAlignment(Pos.CENTER_LEFT);
                    card.setPadding(new Insets(10, 10, 10, 15));
                    card.getStyleClass().add("patient-card");
                    FontIcon icon = new FontIcon("fas-paw");
                    icon.setIconSize(24);
                    icon.setIconColor(Color.web("#D2B48C"));
                    VBox info = new VBox(2);
                    Label lblNombre = new Label(mascota.getNombreMascota());
                    lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1E293B;");
                    String fichaStr = mascota.getNumeroFicha() != null && mascota.getNumeroFicha() > 0 
                             ? "FCH-" + String.format("%03d", mascota.getNumeroFicha()) 
                             : "Sin Ficha";
                    Label lblFicha = new Label(fichaStr);
                    lblFicha.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");
                    info.getChildren().addAll(lblNombre, lblFicha);
                    card.getChildren().addAll(icon, info);
                    if (isSelected()) {
                        card.setStyle("-fx-background-color: #ECFDF5; -fx-border-color: #2CA871; -fx-border-radius: 8; -fx-background-radius: 8;");
                    } else {
                        card.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-background-radius: 8;");
                    }
                    setGraphic(card);
                    setStyle("-fx-background-color: transparent; -fx-padding: 5;");
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
                 ? "FCH-" + String.format("%03d", mascota.getNumeroFicha()) 
                 : "Sin Ficha";
        lblPerfilFicha.setText(fichaStr);
        lblPerfilDetalles.setText(mascota.getRaza() + " • " + mascota.getEspecie() + " • Dueño: " + mascota.getNombreDueno());
        if (mascota.getFechaNacimiento() != null) {
            int edad = Period.between(mascota.getFechaNacimiento(), LocalDate.now()).getYears();
            lblPerfilEdad.setText("Edad: " + edad + " años");
        } else {
            lblPerfilEdad.setText("Edad: Desconocida");
        }
        cargarLineaTiempo(mascota.getId());
    }

    private void cargarLineaTiempo(Long idMascota) {
        contenedorLineaTiempo.getChildren().clear();
        List<TurnoDTO> historial = turnoService.obtenerTodos().stream()
                .filter(t -> t.getIdMascota().equals(idMascota) && t.getEstado().equalsIgnoreCase("ATENDIDO"))
                .sorted((t1, t2) -> t2.getFecha().compareTo(t1.getFecha()))
                .collect(Collectors.toList());
                
        lblPerfilRegistros.setText("Registros: " + historial.size() + " entradas");
        if (historial.isEmpty()) {
            Label vacio = new Label("No hay registros médicos para este paciente.");
            vacio.setStyle("-fx-text-fill: #94A3B8; -fx-font-style: italic; -fx-padding: 20;");
            contenedorLineaTiempo.getChildren().add(vacio);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        for (TurnoDTO turno : historial) {
            HBox tarjeta = new HBox(15);
            tarjeta.setPadding(new Insets(15, 20, 15, 15));
            tarjeta.getStyleClass().add("timeline-card");
            
            boolean esVacuna = turno.getDetallesServicios() != null && turno.getDetallesServicios().contains("Vacunación");
            
            StackPane iconContainer = new StackPane();
            iconContainer.setPrefSize(45, 45);
            iconContainer.setStyle("-fx-background-radius: 8; " + (esVacuna ? "-fx-background-color: #FFF7ED;" : "-fx-background-color: #ECFDF5;"));
            
            FontIcon icon = new FontIcon(esVacuna ? "fas-syringe" : "fas-stethoscope");
            icon.setIconColor(Color.web(esVacuna ? "#F97316" : "#10B981"));
            icon.setIconSize(20);
            iconContainer.getChildren().add(icon);

            VBox contenido = new VBox(5);
            HBox.setHgrow(contenido, Priority.ALWAYS);
            
            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            Label lblTitulo = new Label(esVacuna ? "Aplicación de Vacuna" : "Atención General");
            lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1E293B;");
            
            header.getChildren().add(lblTitulo);
            
            HBox metaInfo = new HBox(15);
            Label lblFecha = new Label("\uD83D\uDCC5 " + turno.getFecha().format(formatter)); 
            Label lblVet = new Label("\uD83D\uDC68\u200D\u2695\uFE0F " + turno.getNombreVeterinario());
            lblFecha.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
            lblVet.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
            metaInfo.getChildren().addAll(lblFecha, lblVet);
            
            // ACÁ MOSTRAMOS EL DIAGNÓSTICO EN GRANDE
            Label lblNotasClinicas = new Label(turno.getDetallesServicios() != null ? turno.getDetallesServicios() : "Sin especificaciones");
            lblNotasClinicas.setWrapText(true);
            lblNotasClinicas.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px; -fx-padding: 8 0 0 0;");
            
            contenido.getChildren().addAll(header, metaInfo, lblNotasClinicas);
            
            tarjeta.setStyle("-fx-background-color: white; -fx-border-color: " + (esVacuna ? "#FDBA74;" : "#6EE7B7;") + " -fx-border-width: 0 0 0 4; -fx-background-radius: 4; -fx-border-radius: 4; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 5, 0, 0, 2);");
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