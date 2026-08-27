package ar.edu.unam.veterinaria.controller;

import ar.edu.unam.veterinaria.dto.*;
import ar.edu.unam.veterinaria.model.Vacuna;
import ar.edu.unam.veterinaria.service.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VacunacionesController {

    // Nuevos controles de la interfaz
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbFiltroVacuna;

    @FXML private Label lblContadorProximos, lblContadorVencidas;
    @FXML private Label lblBurbujaProximos, lblBurbujaVencidas;
    
    @FXML private TableView<AlertaVacunaDTO> tvProximos, tvVencidas;
    @FXML private TableColumn<AlertaVacunaDTO, AlertaVacunaDTO> colProxMascota, colProxPropietario, colProxVacuna, colProxVencimiento, colProxAccion;
    @FXML private TableColumn<AlertaVacunaDTO, AlertaVacunaDTO> colVencMascota, colVencPropietario, colVencVacuna, colVencVencimiento, colVencAccion;

    // Listas Maestras (Guardan los datos originales)
    private ObservableList<AlertaVacunaDTO> masterProximos = FXCollections.observableArrayList();
    private ObservableList<AlertaVacunaDTO> masterVencidas = FXCollections.observableArrayList();
    
    // Listas Filtradas (Se vinculan a las tablas)
    private FilteredList<AlertaVacunaDTO> filteredProximos;
    private FilteredList<AlertaVacunaDTO> filteredVencidas;

    private TurnoService turnoService = new TurnoService();
    private VacunaService vacunaService = new VacunaService();
    private ClienteService clienteService = new ClienteService();

    @FXML
    public void initialize() {
        configurarColumnas();
        
        // Enlazamos las listas filtradas a los datos maestros
        filteredProximos = new FilteredList<>(masterProximos, p -> true);
        filteredVencidas = new FilteredList<>(masterVencidas, p -> true);
        
        tvProximos.setItems(filteredProximos);
        tvVencidas.setItems(filteredVencidas);
        
        cargarDatosDesdeHistorialMedico();
        configurarFiltros();
        vincularContadores();
    }

    private void configurarFiltros() {
        if (cbFiltroVacuna == null || txtBuscar == null) return; // Por seguridad si FXML falla
        
        // 1. Llenamos el ComboBox con los nombres de las vacunas reales
        List<String> nombresVacunas = vacunaService.obtenerTodas().stream()
                .map(Vacuna::getNombreComercial)
                .collect(Collectors.toList());
        nombresVacunas.add(0, "Todas"); // Agregamos la opción por defecto al inicio
        
        cbFiltroVacuna.setItems(FXCollections.observableArrayList(nombresVacunas));
        cbFiltroVacuna.getSelectionModel().selectFirst();

        // 2. Activamos los "escuchadores" en tiempo real
        cbFiltroVacuna.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        String busqueda = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase() : "";
        String vacunaFiltro = cbFiltroVacuna.getValue() != null ? cbFiltroVacuna.getValue() : "Todas";

        java.util.function.Predicate<AlertaVacunaDTO> reglaFiltro = alerta -> {
            // A. Coincidencia de texto (Dueño o Mascota)
            boolean coincideTexto = true;
            if (!busqueda.isEmpty()) {
                coincideTexto = alerta.getNombreMascota().toLowerCase().contains(busqueda) || 
                                alerta.getNombrePropietario().toLowerCase().contains(busqueda);
            }

            // B. Coincidencia de vacuna
            boolean coincideVacuna = true;
            if (!vacunaFiltro.equals("Todas")) {
                coincideVacuna = alerta.getNombreVacuna().equals(vacunaFiltro);
            }

            return coincideTexto && coincideVacuna;
        };

        // Aplicamos la misma regla a ambas tablas instantáneamente
        filteredProximos.setPredicate(reglaFiltro);
        filteredVencidas.setPredicate(reglaFiltro);
    }

    private void vincularContadores() {
        // Vinculamos los contadores a las listas filtradas, así los números cambian al buscar
        filteredProximos.addListener((javafx.collections.ListChangeListener.Change<? extends AlertaVacunaDTO> c) -> {
            int size = filteredProximos.size();
            lblContadorProximos.setText(size + " próximos a vencer");
            lblBurbujaProximos.setText(String.valueOf(size));
        });

        filteredVencidas.addListener((javafx.collections.ListChangeListener.Change<? extends AlertaVacunaDTO> c) -> {
            int size = filteredVencidas.size();
            lblContadorVencidas.setText(size + " vacunas vencidas");
            lblBurbujaVencidas.setText(String.valueOf(size));
        });
        
        // Forzar actualización inicial
        lblContadorProximos.setText(filteredProximos.size() + " próximos a vencer");
        lblBurbujaProximos.setText(String.valueOf(filteredProximos.size()));
        lblContadorVencidas.setText(filteredVencidas.size() + " vacunas vencidas");
        lblBurbujaVencidas.setText(String.valueOf(filteredVencidas.size()));
    }

    private void cargarDatosDesdeHistorialMedico() {
        masterProximos.clear();
        masterVencidas.clear();

        List<TurnoDTO> todosLosTurnos = turnoService.obtenerTodos();
        Map<Long, Vacuna> mapaVacunas = vacunaService.obtenerTodas().stream()
                .collect(Collectors.toMap(Vacuna::getId, v -> v));
        Map<Long, ClienteDTO> mapaClientes = clienteService.obtenerTodos().stream()
                .collect(Collectors.toMap(ClienteDTO::getId, c -> c));

        Map<Long, Map<Long, TurnoDTO>> ultimasVacunas = new HashMap<>();

        for (TurnoDTO t : todosLosTurnos) {
            if ("ATENDIDO".equalsIgnoreCase(t.getEstado()) && t.getIdVacuna() != null) {
                ultimasVacunas.putIfAbsent(t.getIdMascota(), new HashMap<>());
                Map<Long, TurnoDTO> vacunasMascota = ultimasVacunas.get(t.getIdMascota());
                
                TurnoDTO existente = vacunasMascota.get(t.getIdVacuna());
                
                if (existente == null || t.getFecha().isAfter(existente.getFecha())) {
                    vacunasMascota.put(t.getIdVacuna(), t);
                }
            }
        }

        for (Map<Long, TurnoDTO> vacunasMascota : ultimasVacunas.values()) {
            for (TurnoDTO turno : vacunasMascota.values()) {
                Vacuna vac = mapaVacunas.get(turno.getIdVacuna());
                
                if (vac != null && vac.getPeriodicidad() != null) {
                    LocalDate fechaVencimiento = turno.getFecha().plusMonths(vac.getPeriodicidad());
                    long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaVencimiento);
                    
                    if (diasRestantes <= 30) {
                        ClienteDTO cliente = mapaClientes.get(turno.getIdCliente());
                        String telefono = (cliente != null && cliente.getTelefono() != null) ? cliente.getTelefono() : "Sin teléfono";
                        
                        AlertaVacunaDTO alerta = new AlertaVacunaDTO(
                            turno.getIdMascota(),
                            turno.getIdCliente(),
                            turno.getNombreMascota(),
                            turno.getNombreCliente(),
                            telefono,
                            vac.getNombreComercial(),
                            vac.getEnfermedad(),
                            fechaVencimiento,
                            diasRestantes
                        );
                        
                        if (diasRestantes < 0) {
                            masterVencidas.add(alerta);
                        } else {
                            masterProximos.add(alerta);
                        }
                    }
                }
            }
        }

        masterProximos.sort((a, b) -> a.getFechaVencimiento().compareTo(b.getFechaVencimiento()));
        masterVencidas.sort((a, b) -> a.getFechaVencimiento().compareTo(b.getFechaVencimiento()));
    }

    private void configurarColumnas() {
        configurarTabla(colProxMascota, colProxPropietario, colProxVacuna, colProxVencimiento, colProxAccion, false);
        configurarTabla(colVencMascota, colVencPropietario, colVencVacuna, colVencVencimiento, colVencAccion, true);
    }

    private void configurarTabla(
            TableColumn<AlertaVacunaDTO, AlertaVacunaDTO> colMas, TableColumn<AlertaVacunaDTO, AlertaVacunaDTO> colProp, 
            TableColumn<AlertaVacunaDTO, AlertaVacunaDTO> colVac, TableColumn<AlertaVacunaDTO, AlertaVacunaDTO> colVenc, 
            TableColumn<AlertaVacunaDTO, AlertaVacunaDTO> colAcc, boolean esVencida) {
        
        colMas.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));
        colProp.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));
        colVac.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));
        colVenc.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));
        colAcc.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));

        colMas.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(AlertaVacunaDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); } else {
                    HBox box = new HBox(8); box.setAlignment(Pos.CENTER_LEFT);
                    FontIcon icon = new FontIcon("fas-dog"); icon.setIconColor(Color.web("#D2B48C"));
                    Label lbl = new Label(item.getNombreMascota());
                    lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
                    box.getChildren().addAll(icon, lbl); setGraphic(box);
                }
            }
        });

        colProp.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(AlertaVacunaDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); } else {
                    VBox box = new VBox(2);
                    Label lblNombre = new Label(item.getNombrePropietario());
                    lblNombre.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
                    HBox boxTel = new HBox(5); boxTel.setAlignment(Pos.CENTER_LEFT);
                    FontIcon phone = new FontIcon("fas-phone-alt"); phone.setIconColor(Color.web("#94A3B8"));
                    Label lblTel = new Label(item.getTelefonoPropietario());
                    lblTel.setStyle("-fx-text-fill: #64748B;");
                    boxTel.getChildren().addAll(phone, lblTel);
                    box.getChildren().addAll(lblNombre, boxTel); setGraphic(box);
                }
            }
        });

        colVac.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(AlertaVacunaDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); } else {
                    HBox box = new HBox(8); box.setAlignment(Pos.CENTER_LEFT);
                    FontIcon icon = new FontIcon("fas-syringe"); icon.setIconColor(Color.web("#D2B48C"));
                    Label lbl = new Label(item.getNombreVacuna() + " (" + item.getEnfermedadVacuna() + ")");
                    box.getChildren().addAll(icon, lbl); setGraphic(box);
                }
            }
        });

        colVenc.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(AlertaVacunaDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); } else {
                    VBox box = new VBox(2);
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd 'de' MMM 'de' yyyy");
                    Label lblFecha = new Label(item.getFechaVencimiento().format(fmt));
                    lblFecha.setStyle(esVencida ? "-fx-text-fill: #EF4444;" : "-fx-font-weight: bold; -fx-text-fill: #F59E0B;");
                    
                    long d = item.getDiasRestantes();
                    Label lblDias = new Label(esVencida ? "HACE " + Math.abs(d) + " DÍAS" : (d == 0 ? "¡VENCE HOY!" : "en " + d + " días"));
                    lblDias.setStyle(esVencida ? "-fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-font-size: 11px;" : "-fx-text-fill: #94A3B8; -fx-font-size: 11px;");
                    
                    box.getChildren().addAll(lblFecha, lblDias); setGraphic(box);
                }
            }
        });

        colAcc.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(AlertaVacunaDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); } else {
                    Button btn = new Button("+ Agendar");
                    btn.getStyleClass().add("btn-primary");
                    btn.setStyle("-fx-padding: 4 12; -fx-font-size: 11px; -fx-background-color: #2CA871;");
                    
                    btn.setOnAction(e -> agendarTurno(item));
                    setGraphic(btn);
                }
            }
        });
    }

    private void agendarTurno(AlertaVacunaDTO item) {
        TurnoController.preCargaClienteId = item.getIdCliente();
        TurnoController.preCargaMascotaId = item.getIdMascota();
        TurnoController.preCargaVacuna = item.getNombreVacuna();

        Button btnTurnos = (Button) tvProximos.getScene().lookup("#btnTurnos");
        if (btnTurnos != null) {
            btnTurnos.fire();
        }
    }
}