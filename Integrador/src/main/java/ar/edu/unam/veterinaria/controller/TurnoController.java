package ar.edu.unam.veterinaria.controller;

import ar.edu.unam.veterinaria.dto.*;
import ar.edu.unam.veterinaria.exception.CancelacionFueradeTermino;
import ar.edu.unam.veterinaria.exception.TurnoSolapado;
import ar.edu.unam.veterinaria.exception.VeterinarioNoDisponible;
import ar.edu.unam.veterinaria.service.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TurnoController {

    private Long idTurnoEnEdicion = null; 
    private LocalDate fechaInicioCalendario = LocalDate.now();
    private LocalDate fechaSeleccionada = LocalDate.now();

    private ClienteService clienteService = new ClienteService();
    private MascotaService mascotaService = new MascotaService();
    private VeterinarioService veterinarioService = new VeterinarioService();
    private TurnoService turnoService = new TurnoService(); 

    @FXML private HBox overlayFormulario;
    @FXML private HBox overlayDetalles;
    @FXML private Label lblTituloFormulario;
    @FXML private Button btnGuardarFormulario;

    @FXML private Label lblTurnosActivos;
    @FXML private Label lblCountPendiente;
    @FXML private Label lblCountConfirmado; 
    @FXML private Label lblCountAsistio;
    @FXML private Label lblCountCancelado;
    
    @FXML private HBox hboxCalendario;
    @FXML private Label lblMesAnio;

    @FXML private ComboBox<ClienteDTO> cbCliente;
    @FXML private ComboBox<MascotaDTO> cbMascota;
    @FXML private ComboBox<VeterinarioDTO> cbVeterinario;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtHora;
    
    // --- CHECKBOXES ---
    @FXML private CheckBox chkConsulta;
    @FXML private CheckBox chkVacunacion;
    @FXML private CheckBox chkDesparasitacion;
    @FXML private CheckBox chkCirugia;
    @FXML private CheckBox chkEcografia;
    @FXML private CheckBox chkAnalisis;
    
    @FXML private TextArea txtNotas;

    // --- DETALLES ---
    @FXML private Label lblDetalleEstado;
    @FXML private Label lblDetalleMascota;
    @FXML private Label lblDetalleCliente;
    @FXML private Label lblDetalleFecha;
    @FXML private Label lblDetalleHora;
    @FXML private Label lblDetalleVet;
    @FXML private Label lblDetalleMotivo;
    @FXML private Label lblDetalleCosto; // <-- VARIABLE AGREGADA

    @FXML private TableView<TurnoDTO> tvTurnos;
    @FXML private TableColumn<TurnoDTO, String> colHora;
    @FXML private TableColumn<TurnoDTO, String> colMascota;
    @FXML private TableColumn<TurnoDTO, String> colVeterinario;
    @FXML private TableColumn<TurnoDTO, String> colServicios;
    @FXML private TableColumn<TurnoDTO, String> colEstado;
    @FXML private TableColumn<TurnoDTO, Void> colAcciones; 

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarConvertidoresComboBox();
        cargarDatosDesdeBD();
        configurarFiltroMascotas();
        generarCalendario();
        cargarTabla(); 
    }

    private void generarCalendario() {
        hboxCalendario.getChildren().clear();
        String mes = fechaInicioCalendario.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES")).toUpperCase();
        lblMesAnio.setText(mes + " " + fechaInicioCalendario.getYear());
        
        for(int i = 0; i < 7; i++) {
            LocalDate dia = fechaInicioCalendario.plusDays(i);
            VBox boxDia = new VBox();
            boxDia.getStyleClass().add("timeline-day");
            
            if (dia.equals(fechaSeleccionada)) {
                boxDia.getStyleClass().add("timeline-day-active");
            }
            
            String nombreDia = dia.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("es", "ES")).toUpperCase();
            Label lblDiaSemana = new Label(nombreDia);
            lblDiaSemana.getStyleClass().add("timeline-text-day");
            Label lblNumero = new Label(String.valueOf(dia.getDayOfMonth()));
            lblNumero.getStyleClass().add("timeline-text-num");
            
            boxDia.getChildren().addAll(lblDiaSemana, lblNumero);
            boxDia.setOnMouseClicked(e -> {
                fechaSeleccionada = dia;
                generarCalendario(); 
                cargarTabla(); 
            });
            hboxCalendario.getChildren().add(boxDia);
        }
    }

    @FXML public void semanaSiguiente() { fechaInicioCalendario = fechaInicioCalendario.plusDays(7); generarCalendario(); }
    @FXML public void semanaAnterior() { fechaInicioCalendario = fechaInicioCalendario.minusDays(7); generarCalendario(); }

    private void configurarColumnas() {
        colHora.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getHora().toString()));
        colHora.setCellFactory(column -> new TableCell<TurnoDTO, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); setText(null); }
                else {
                    Label lbl = new Label(item);
                    lbl.setStyle("-fx-font-size: 15px; -fx-font-weight: 900; -fx-text-fill: #1E293B;");
                    setGraphic(lbl);
                }
            }
        });

        colMascota.setCellValueFactory(new PropertyValueFactory<>("nombreMascota"));
        colMascota.setCellFactory(column -> new TableCell<TurnoDTO, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); setText(null); }
                else {
                    HBox box = new HBox(8); box.setAlignment(Pos.CENTER_LEFT);
                    FontIcon icon = new FontIcon("fas-dog"); icon.setIconColor(Color.web("#D2B48C")); icon.setIconSize(16);
                    Label lbl = new Label(item); lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B; -fx-font-size: 13px;");
                    box.getChildren().addAll(icon, lbl);
                    setGraphic(box);
                }
            }
        });

        colVeterinario.setCellValueFactory(new PropertyValueFactory<>("nombreVeterinario"));

        colServicios.setCellValueFactory(new PropertyValueFactory<>("detallesServicios"));
        colServicios.setCellFactory(column -> new TableCell<TurnoDTO, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) { setGraphic(null); setText(null); }
                else {
                    Label lbl = new Label(item);
                    lbl.getStyleClass().add("pill-outline");
                    setGraphic(lbl);
                }
            }
        });

        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(column -> new TableCell<TurnoDTO, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); } 
                else {
                    Label lbl = new Label("• " + item);
                    lbl.setStyle("-fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 12; -fx-font-size: 11px;");
                    if (item.equals("PENDIENTE")) { lbl.setStyle(lbl.getStyle() + "-fx-background-color: #FEF08A; -fx-text-fill: #854D0E;"); } 
                    else if (item.equals("CONFIRMADO")) { lbl.setStyle(lbl.getStyle() + "-fx-background-color: #BFDBFE; -fx-text-fill: #1E3A8A;"); } 
                    else if (item.equals("ATENDIDO")) { lbl.setStyle(lbl.getStyle() + "-fx-background-color: #D1FAE5; -fx-text-fill: #065F46;"); } 
                    else { lbl.setStyle(lbl.getStyle() + "-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B;"); }
                    setGraphic(lbl);
                }
            }
        });

        colAcciones.setCellFactory(column -> new TableCell<TurnoDTO, Void>() {
            private final Button btnVer = new Button(" Ver");
            private final Button btnAccionPrincipal = new Button(); 
            private final Button btnEditar = new Button(" Editar");
            private final Button btnCancelar = new Button(" Cancelar");
            private final HBox pane = new HBox(8, btnVer, btnAccionPrincipal, btnEditar, btnCancelar);

            {
                pane.setAlignment(Pos.CENTER_LEFT);
                btnVer.getStyleClass().addAll("btn-action-small", "btn-editar");
                FontIcon iconView = new FontIcon("fas-eye"); iconView.setIconColor(Color.web("#64748B"));
                btnVer.setGraphic(iconView);

                btnAccionPrincipal.getStyleClass().addAll("btn-action-small");
                FontIcon iconCheck = new FontIcon("fas-check"); iconCheck.setIconColor(Color.WHITE);
                btnAccionPrincipal.setGraphic(iconCheck);

                btnEditar.getStyleClass().addAll("btn-action-small", "btn-editar");
                FontIcon iconEdit = new FontIcon("fas-pen"); iconEdit.setIconColor(Color.web("#94A3B8"));
                btnEditar.setGraphic(iconEdit);

                btnCancelar.getStyleClass().addAll("btn-action-small", "btn-cancelar");

                btnVer.setOnAction(e -> abrirDetalles(getTableView().getItems().get(getIndex())));

                btnAccionPrincipal.setOnAction(e -> {
                    TurnoDTO t = getTableView().getItems().get(getIndex());
                    if (t.getEstado().equals("PENDIENTE")) turnoService.confirmarTurno(t.getId());
                    else if (t.getEstado().equals("CONFIRMADO")) turnoService.atenderTurno(t.getId());
                    cargarTabla();
                });

                btnEditar.setOnAction(e -> abrirEdicion(getTableView().getItems().get(getIndex())));

                btnCancelar.setOnAction(e -> {
                    try {
                        turnoService.cancelarTurno(getTableView().getItems().get(getIndex()).getId());
                        cargarTabla();
                    } catch (CancelacionFueradeTermino ex) {
                        mostrarAlerta("Cancelación Rechazada", ex.getMessage(), Alert.AlertType.WARNING);
                    }
                });
            }

            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); } 
                else {
                    TurnoDTO turno = getTableView().getItems().get(getIndex());
                    boolean esPendiente = turno.getEstado().equals("PENDIENTE");
                    boolean esConfirmado = turno.getEstado().equals("CONFIRMADO");
                    
                    if (esPendiente) {
                        btnAccionPrincipal.setText(" Confirmar");
                        btnAccionPrincipal.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white;"); 
                        btnAccionPrincipal.setDisable(false);
                    } else if (esConfirmado) {
                        btnAccionPrincipal.setText(" Atender");
                        btnAccionPrincipal.setStyle("-fx-background-color: #10B981; -fx-text-fill: white;"); 
                        btnAccionPrincipal.setDisable(false);
                    } else {
                        btnAccionPrincipal.setText(" Finalizado");
                        btnAccionPrincipal.setStyle("-fx-background-color: #94A3B8; -fx-text-fill: white;"); 
                        btnAccionPrincipal.setDisable(true);
                    }

                    btnCancelar.setDisable(!(esPendiente || esConfirmado));
                    btnEditar.setDisable(!(esPendiente || esConfirmado));
                    
                    if(!(esPendiente || esConfirmado)) {
                        FontIcon iconLock = new FontIcon("fas-lock"); iconLock.setIconColor(Color.web("#CBD5E1"));
                        btnCancelar.setGraphic(iconLock);
                    } else {
                        FontIcon iconCancel = new FontIcon("fas-times"); iconCancel.setIconColor(Color.web("#94A3B8"));
                        btnCancelar.setGraphic(iconCancel);
                    }
                    setGraphic(pane);
                }
            }
        });
    }

    private void cargarTabla() {
        List<TurnoDTO> filtrados = turnoService.obtenerTodos().stream()
            .filter(t -> t.getFecha().equals(fechaSeleccionada))
            .collect(Collectors.toList());
        tvTurnos.setItems(FXCollections.observableArrayList(filtrados));
        actualizarContadores(filtrados);
    }

    private void actualizarContadores(List<TurnoDTO> listaTurnos) {
        if (listaTurnos == null) return;
        long pendientes = 0, confirmados = 0, asistieron = 0, cancelados = 0;

        for (TurnoDTO turno : listaTurnos) {
            String estado = turno.getEstado().toUpperCase();
            if (estado.equals("PENDIENTE")) pendientes++;
            else if (estado.equals("CONFIRMADO")) confirmados++;
            else if (estado.equals("ATENDIDO")) asistieron++;
            else cancelados++;
        }
        lblCountPendiente.setText(String.valueOf(pendientes));
        lblCountConfirmado.setText(String.valueOf(confirmados));
        lblCountAsistio.setText(String.valueOf(asistieron));
        lblCountCancelado.setText(String.valueOf(cancelados));
        
        String fechaFormateada = fechaSeleccionada.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES")));
        lblTurnosActivos.setText(fechaFormateada + " · " + listaTurnos.size() + " turnos activos");
    }

    private void cargarDatosDesdeBD() {
        cbCliente.setItems(FXCollections.observableArrayList(clienteService.obtenerTodos()));
        cbVeterinario.setItems(FXCollections.observableArrayList(veterinarioService.obtenerTodos()));
    }

    private void configurarFiltroMascotas() {
        cbCliente.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                List<MascotaDTO> mascotasDelCliente = mascotaService.obtenerTodas().stream()
                        .filter(m -> m.getIdCliente() != null && m.getIdCliente().equals(nuevo.getId()))
                        .collect(Collectors.toList());
                cbMascota.setItems(FXCollections.observableArrayList(mascotasDelCliente));
                cbMascota.setDisable(false);
            } else {
                cbMascota.getItems().clear();
                cbMascota.setDisable(true);
            }
        });
    }

    private void configurarConvertidoresComboBox() {
        cbCliente.setConverter(new StringConverter<ClienteDTO>() {
            @Override public String toString(ClienteDTO cliente) { return cliente == null ? "" : cliente.getNombre() + " " + cliente.getApellido(); }
            @Override public ClienteDTO fromString(String string) { return null; }
        });
        cbMascota.setConverter(new StringConverter<MascotaDTO>() {
            @Override public String toString(MascotaDTO mascota) { return mascota == null ? "" : mascota.getNombreMascota() + " (" + mascota.getEspecie() + ")"; }
            @Override public MascotaDTO fromString(String string) { return null; }
        });
        cbVeterinario.setConverter(new StringConverter<VeterinarioDTO>() {
            @Override public String toString(VeterinarioDTO vet) { return vet == null ? "" : "Dr/a. " + vet.getNombre() + " " + vet.getApellido(); }
            @Override public VeterinarioDTO fromString(String string) { return null; }
        });
    }

    @FXML
    public void guardarTurno(ActionEvent event) {
        if (cbMascota.getValue() == null || cbVeterinario.getValue() == null || dpFecha.getValue() == null || txtHora.getText().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Complete todos los datos obligatorios.", Alert.AlertType.WARNING);
            return;
        }

        LocalTime horaParsed;
        try {
            horaParsed = LocalTime.parse(txtHora.getText().trim());
        } catch (Exception e) {
            mostrarAlerta("Hora Inválida", "Formato de hora HH:mm (ej. 10:30).", Alert.AlertType.WARNING);
            return;
        }

        List<String> serviciosSeleccionados = new ArrayList<>();
        if (chkConsulta.isSelected()) serviciosSeleccionados.add(chkConsulta.getText());
        if (chkVacunacion.isSelected()) serviciosSeleccionados.add(chkVacunacion.getText());
        if (chkDesparasitacion.isSelected()) serviciosSeleccionados.add(chkDesparasitacion.getText());
        if (chkCirugia.isSelected()) serviciosSeleccionados.add(chkCirugia.getText());
        if (chkEcografia.isSelected()) serviciosSeleccionados.add(chkEcografia.getText());
        if (chkAnalisis.isSelected()) serviciosSeleccionados.add(chkAnalisis.getText());

        if (serviciosSeleccionados.isEmpty()) {
            mostrarAlerta("Falta Servicio", "Debe seleccionar al menos una práctica médica a realizar.", Alert.AlertType.WARNING);
            return;
        }

        // ACÁ ESTABA EL ERROR DEL CONSTRUCTOR: Faltaba el 0.0 al final
        TurnoDTO dto = new TurnoDTO(
            idTurnoEnEdicion != null ? idTurnoEnEdicion : 0L, 
            dpFecha.getValue(), 
            horaParsed, 
            "PENDIENTE", 
            cbMascota.getValue().getId(), null, 
            cbVeterinario.getValue().getId(), null, 
            cbCliente.getValue().getId(), null,
            "", 
            serviciosSeleccionados, 
            txtNotas.getText(),
            0.0 
        );

        try {
            TurnoDTO guardado = (idTurnoEnEdicion == null) ? turnoService.guardarTurno(dto) : turnoService.actualizarTurno(dto);

            if (guardado == null) {
                mostrarAlerta("Error", "Fallo al guardar el turno.", Alert.AlertType.ERROR);
                return;
            }

            mostrarAlerta("Éxito", "Operación realizada correctamente.", Alert.AlertType.INFORMATION);
            fechaSeleccionada = guardado.getFecha();
            fechaInicioCalendario = guardado.getFecha();
            generarCalendario(); 
            cerrarModal();
            cargarTabla(); 

        } catch (VeterinarioNoDisponible | TurnoSolapado ex) {
            // Atrapamos las dos reglas de negocio de tu UML
            mostrarAlerta("Atención", ex.getMessage(), Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void abrirPanelFormulario() {
        idTurnoEnEdicion = null;
        lblTituloFormulario.setText("Crear Nuevo Turno");
        btnGuardarFormulario.setText("Confirmar Turno");
        
        cbCliente.getSelectionModel().clearSelection();
        cbMascota.getSelectionModel().clearSelection();
        cbMascota.setDisable(true);
        cbVeterinario.getSelectionModel().clearSelection();
        dpFecha.setValue(fechaSeleccionada); 
        txtHora.clear();
        txtNotas.clear();
        
        chkConsulta.setSelected(false);
        chkVacunacion.setSelected(false);
        chkDesparasitacion.setSelected(false);
        chkCirugia.setSelected(false);
        chkEcografia.setSelected(false);
        chkAnalisis.setSelected(false);

        overlayFormulario.setVisible(true);
    }

    private void abrirEdicion(TurnoDTO t) {
        idTurnoEnEdicion = t.getId();
        lblTituloFormulario.setText("Reprogramar Turno");
        btnGuardarFormulario.setText("Guardar Cambios");

        cbCliente.getItems().stream().filter(c -> c.getId() == t.getIdCliente()).findFirst().ifPresent(cbCliente.getSelectionModel()::select);
        cbMascota.getItems().stream().filter(m -> m.getId().equals(t.getIdMascota())).findFirst().ifPresent(cbMascota.getSelectionModel()::select);
        cbVeterinario.getItems().stream().filter(v -> v.getId().equals(t.getIdVeterinario())).findFirst().ifPresent(cbVeterinario.getSelectionModel()::select);
        
        dpFecha.setValue(t.getFecha());
        txtHora.setText(t.getHora().toString());
        
        String detalles = t.getDetallesServicios() != null ? t.getDetallesServicios() : "";
        chkConsulta.setSelected(detalles.contains("Consulta"));
        chkVacunacion.setSelected(detalles.contains("Vacunación"));
        chkDesparasitacion.setSelected(detalles.contains("Desparasitación"));
        chkCirugia.setSelected(detalles.contains("Cirugía"));
        chkEcografia.setSelected(detalles.contains("Ecografía"));
        chkAnalisis.setSelected(detalles.contains("Análisis"));
        
        txtNotas.clear(); 

        overlayFormulario.setVisible(true);
    }

    private void abrirDetalles(TurnoDTO t) {
        lblDetalleEstado.setText(t.getEstado());
        lblDetalleMascota.setText(t.getNombreMascota());
        lblDetalleCliente.setText("Dueño: " + t.getNombreCliente());
        lblDetalleFecha.setText("Fecha: " + t.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblDetalleHora.setText("Hora: " + t.getHora().toString());
        lblDetalleVet.setText("Atiende: " + t.getNombreVeterinario());
        lblDetalleMotivo.setText(t.getDetallesServicios() != null ? t.getDetallesServicios() : "Sin especificaciones.");
        
        // Muestra el costo formateado
        if (lblDetalleCosto != null) {
            lblDetalleCosto.setText(String.format("$ %.2f", t.getCostoTotal() != null ? t.getCostoTotal() : 0.0));
        }
        
        overlayDetalles.setVisible(true);
    }

    @FXML
    public void cerrarModal() {
        overlayFormulario.setVisible(false);
        overlayDetalles.setVisible(false);
        idTurnoEnEdicion = null;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}