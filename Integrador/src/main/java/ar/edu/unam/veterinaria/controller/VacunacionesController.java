package ar.edu.unam.veterinaria.controller;

import ar.edu.unam.veterinaria.dto.*;
import ar.edu.unam.veterinaria.model.Vacuna;
import ar.edu.unam.veterinaria.service.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    @FXML private Label lblContadorProximos, lblContadorVencidas;
    @FXML private Label lblBurbujaProximos, lblBurbujaVencidas;
    
    @FXML private TableView<AlertaVacunaDTO> tvProximos, tvVencidas;
    @FXML private TableColumn<AlertaVacunaDTO, AlertaVacunaDTO> colProxMascota, colProxPropietario, colProxVacuna, colProxVencimiento, colProxAccion;
    @FXML private TableColumn<AlertaVacunaDTO, AlertaVacunaDTO> colVencMascota, colVencPropietario, colVencVacuna, colVencVencimiento, colVencAccion;

    private ObservableList<AlertaVacunaDTO> listaProximos = FXCollections.observableArrayList();
    private ObservableList<AlertaVacunaDTO> listaVencidas = FXCollections.observableArrayList();

    // Servicios
    private TurnoService turnoService = new TurnoService();
    private VacunaService vacunaService = new VacunaService();
    private ClienteService clienteService = new ClienteService();

    @FXML
    public void initialize() {
        configurarColumnas();
        vincularContadores();
        cargarDatosDesdeHistorialMedico();
    }

    private void vincularContadores() {
        listaProximos.addListener((javafx.collections.ListChangeListener.Change<? extends AlertaVacunaDTO> c) -> {
            int size = listaProximos.size();
            lblContadorProximos.setText(size + " próximos a vencer");
            lblBurbujaProximos.setText(String.valueOf(size));
        });

        listaVencidas.addListener((javafx.collections.ListChangeListener.Change<? extends AlertaVacunaDTO> c) -> {
            int size = listaVencidas.size();
            lblContadorVencidas.setText(size + " vacunas vencidas");
            lblBurbujaVencidas.setText(String.valueOf(size));
        });
    }

    private void cargarDatosDesdeHistorialMedico() {
        listaProximos.clear();
        listaVencidas.clear();

        // 1. Obtener todos los turnos, vacunas y clientes de la base de datos
        List<TurnoDTO> todosLosTurnos = turnoService.obtenerTodos();
        Map<Long, Vacuna> mapaVacunas = vacunaService.obtenerTodas().stream()
                .collect(Collectors.toMap(Vacuna::getId, v -> v));
        Map<Long, ClienteDTO> mapaClientes = clienteService.obtenerTodos().stream()
                .collect(Collectors.toMap(ClienteDTO::getId, c -> c));

        // 2. Agrupar para encontrar la ÚLTIMA aplicación de cada vacuna por mascota
        // Estructura: Map<IdMascota, Map<IdVacuna, TurnoMasReciente>>
        Map<Long, Map<Long, TurnoDTO>> ultimasVacunas = new HashMap<>();

        for (TurnoDTO t : todosLosTurnos) {
            // Filtramos SOLO los turnos ya Atendidos que contengan una Vacuna
            if ("ATENDIDO".equalsIgnoreCase(t.getEstado()) && t.getIdVacuna() != null) {
                ultimasVacunas.putIfAbsent(t.getIdMascota(), new HashMap<>());
                Map<Long, TurnoDTO> vacunasMascota = ultimasVacunas.get(t.getIdMascota());
                
                TurnoDTO existente = vacunasMascota.get(t.getIdVacuna());
                // Si es la primera vez que vemos esta vacuna, o si la fecha es MÁS RECIENTE, la guardamos.
                if (existente == null || t.getFecha().isAfter(existente.getFecha())) {
                    vacunasMascota.put(t.getIdVacuna(), t);
                }
            }
        }

        // 3. Calcular los vencimientos
        for (Map<Long, TurnoDTO> vacunasMascota : ultimasVacunas.values()) {
            for (TurnoDTO turno : vacunasMascota.values()) {
                Vacuna vac = mapaVacunas.get(turno.getIdVacuna());
                
                if (vac != null && vac.getPeriodicidad() != null) {
                    // Sumamos los meses de periodicidad configurada a la fecha en la que se aplicó
                    LocalDate fechaVencimiento = turno.getFecha().plusMonths(vac.getPeriodicidad());
                    long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaVencimiento);
                    
                    // Si faltan 30 días o menos (o ya es negativo, o sea, venció)
                    if (diasRestantes <= 30) {
                        ClienteDTO cliente = mapaClientes.get(turno.getIdCliente());
                        String telefono = (cliente != null && cliente.getTelefono() != null) ? cliente.getTelefono() : "Sin teléfono";
                        
                        AlertaVacunaDTO alerta = new AlertaVacunaDTO(
                            turno.getIdMascota(),
                            turno.getNombreMascota(),
                            turno.getNombreCliente(),
                            telefono,
                            vac.getNombreComercial() + " (" + vac.getEnfermedad() + ")",
                            fechaVencimiento,
                            diasRestantes
                        );
                        
                        // Clasificamos en listas
                        if (diasRestantes < 0) {
                            listaVencidas.add(alerta);
                        } else {
                            listaProximos.add(alerta);
                        }
                    }
                }
            }
        }

        // Ordenamos las listas para que lo más urgente aparezca primero
        listaProximos.sort((a, b) -> a.getFechaVencimiento().compareTo(b.getFechaVencimiento()));
        listaVencidas.sort((a, b) -> a.getFechaVencimiento().compareTo(b.getFechaVencimiento()));

        tvProximos.setItems(listaProximos);
        tvVencidas.setItems(listaVencidas);
    }

    private void configurarColumnas() {
        configurarTabla(colProxMascota, colProxPropietario, colProxVacuna, colProxVencimiento, colProxAccion, false);
        configurarTabla(colVencMascota, colVencPropietario, colVencVacuna, colVencVencimiento, colVencAccion, true);
    }

    private void configurarTabla(
            TableColumn<AlertaVacunaDTO, AlertaVacunaDTO> colMas, 
            TableColumn<AlertaVacunaDTO, AlertaVacunaDTO> colProp, 
            TableColumn<AlertaVacunaDTO, AlertaVacunaDTO> colVac, 
            TableColumn<AlertaVacunaDTO, AlertaVacunaDTO> colVenc, 
            TableColumn<AlertaVacunaDTO, AlertaVacunaDTO> colAcc, 
            boolean esVencida) {
        
        colMas.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));
        colProp.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));
        colVac.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));
        colVenc.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));
        colAcc.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));

        colMas.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(AlertaVacunaDTO item, boolean empty) {
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
            @Override
            protected void updateItem(AlertaVacunaDTO item, boolean empty) {
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
            @Override
            protected void updateItem(AlertaVacunaDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); } else {
                    HBox box = new HBox(8); box.setAlignment(Pos.CENTER_LEFT);
                    FontIcon icon = new FontIcon("fas-syringe"); icon.setIconColor(Color.web("#D2B48C"));
                    Label lbl = new Label(item.getNombreVacuna());
                    box.getChildren().addAll(icon, lbl); setGraphic(box);
                }
            }
        });

        colVenc.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(AlertaVacunaDTO item, boolean empty) {
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
            @Override
            protected void updateItem(AlertaVacunaDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); } else {
                    Button btn = new Button("+ Registrar");
                    btn.getStyleClass().add("btn-primary");
                    btn.setStyle("-fx-padding: 4 12; -fx-font-size: 11px; -fx-background-color: #2CA871;");
                    setGraphic(btn);
                }
            }
        });
    }
}