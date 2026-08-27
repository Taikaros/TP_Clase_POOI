package ar.edu.unam.veterinaria.controller;

import ar.edu.unam.veterinaria.AppVeterinaria;
import ar.edu.unam.veterinaria.dto.ClienteDTO;
import ar.edu.unam.veterinaria.dto.GuarderiaDTO;
import ar.edu.unam.veterinaria.dto.MascotaDTO;
import ar.edu.unam.veterinaria.dto.PeluqueriaDTO;
import ar.edu.unam.veterinaria.exception.CancelacionFueradeTermino;
import ar.edu.unam.veterinaria.model.EstadoTurno;
import ar.edu.unam.veterinaria.model.TipoServicio;
import ar.edu.unam.veterinaria.model.Turno;
import ar.edu.unam.veterinaria.service.ClienteService;
import ar.edu.unam.veterinaria.service.GuarderiaPeluqueriaService;
import ar.edu.unam.veterinaria.service.MascotaService;
import ar.edu.unam.veterinaria.service.TipoServicioService;
import ar.edu.unam.veterinaria.service.TurnoService;
import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.function.UnaryOperator;

public class GuarderiaPeluqueriaController {

    private GuarderiaPeluqueriaService gpService = new GuarderiaPeluqueriaService();
    private ClienteService clienteService = new ClienteService();
    private MascotaService mascotaService = new MascotaService();
    private TurnoService turnoService = new TurnoService();
    private TipoServicioService tipoServicioService = new TipoServicioService();

    @FXML private TableView<GuarderiaDTO> tvGuarderia;
    @FXML private TableColumn<GuarderiaDTO, String> colGuarderiaMascota;
    @FXML private TableColumn<GuarderiaDTO, LocalDate> colGuarderiaIngreso;
    @FXML private TableColumn<GuarderiaDTO, LocalTime> colGuarderiaHora;
    @FXML private TableColumn<GuarderiaDTO, String> colGuarderiaTiempo;
    @FXML private TableColumn<GuarderiaDTO, String> colGuarderiaObs;

    @FXML private TableView<PeluqueriaDTO> tvPeluqueria;
    @FXML private TableColumn<PeluqueriaDTO, String> colPeluqueriaMascota;
    @FXML private TableColumn<PeluqueriaDTO, String> colPeluqueriaServicio;
    @FXML private TableColumn<PeluqueriaDTO, LocalTime> colPeluqueriaHora;
    @FXML private TableColumn<PeluqueriaDTO, String> colPeluqueriaEstado;
    @FXML private TableColumn<PeluqueriaDTO, String> colPeluqueriaObs;

    @FXML private Label lblCuposTotales;
    @FXML private Label lblJaulasOcupadas;
    @FXML private Label lblJaulasLibres;
    private final int TOTAL_JAULAS = 12;

    @FXML private HBox overlayFormulario;
    @FXML private Label lblTituloFormulario;
    @FXML private ComboBox<ClienteDTO> cbCliente;
    @FXML private ComboBox<MascotaDTO> cbMascota;
    @FXML private DatePicker dpFecha;
    @FXML private VBox boxFechaSalida;
    @FXML private DatePicker dpFechaSalida;
    @FXML private ComboBox<String> cbHora;
    @FXML private Label lblTipoServicio;
    
    @FXML private ComboBox<Object> cbTipoServicio; 
    
    @FXML private VBox boxGuarderiaExtras;
    @FXML private TextField txtAlimentacion;
    @FXML private CheckBox chkActividad;
    
    @FXML private TextArea txtObservaciones;

    @FXML private HBox overlayDetalles;
    @FXML private Label lblDetalleNombre;
    @FXML private Label lblDetalleDueno;
    @FXML private Label lblDetalleEspecie;
    @FXML private Label lblDetalleRaza;
    @FXML private Label lblDetalleEdad;
    @FXML private Label lblDetalleFicha;
    @FXML private Label lblDetalleTurnoFecha;
    @FXML private Label lblDetalleTurnoHora;
    @FXML private Label lblDetalleServicioEstado;
    @FXML private Label lblDetalleObservaciones;
    
    @FXML private Label lblDetallePrecio;
    @FXML private VBox boxDetallesExtras;
    @FXML private Label lblDetalleActividad;
    @FXML private VBox boxDetallesAlimentacion;
    @FXML private Label lblDetalleAlimentacion;
    
    @FXML private Button btnConfirmar;
    @FXML private Button btnCancelar;
    @FXML private Button btnEliminar;

    private String modoFormulario = ""; 
    private Long idTurnoSeleccionado = null; 

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarComboBoxes();
        configurarCalendario();
        cargarDatos();
    }

   private void configurarCalendario() {
        // --- 1. FILTRO DE TEXTO PARA AMBOS DATEPICKERS (Solo números y barras) ---
        UnaryOperator<TextFormatter.Change> filtroFecha = change -> {
            if (change.getControlNewText().matches("[0-9/]*")) {
                return change;
            }
            return null; // Si intentan poner letras, se bloquea
        };
        dpFecha.getEditor().setTextFormatter(new TextFormatter<>(filtroFecha));
        dpFechaSalida.getEditor().setTextFormatter(new TextFormatter<>(filtroFecha));

        // --- 2. FORMATEADOR VISUAL PARA AMBOS DATEPICKERS (dd/MM/yyyy) ---
        StringConverter<LocalDate> convertidorFecha = new StringConverter<LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            @Override public String toString(LocalDate date) { return date != null ? formatter.format(date) : ""; }
            @Override public LocalDate fromString(String string) { return (string == null || string.isEmpty()) ? null : LocalDate.parse(string, formatter); }
        };
        dpFecha.setConverter(convertidorFecha);
        dpFechaSalida.setConverter(convertidorFecha);

        // --- 3. REGLAS VISUALES DEL CALENDARIO DE INGRESO ---
        dpFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // Bloquear las fechas pasadas (no se puede agendar hacia atrás)
                if (empty || date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #f3f4f6; -fx-text-fill: #9ca3af;");
                }
                // Bloquear visualmente los domingos para el INGRESO
                if (date != null && date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    setDisable(true);
                    setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #ef4444;"); // Fondo rojizo sutil
                }
            }
        });
        
        // Validación extra: Si el usuario tipea a mano una fecha pasada o un domingo en dpFecha
        dpFecha.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (newVal.isBefore(LocalDate.now())) {
                    dpFecha.setValue(oldVal);
                    mostrarAlerta("Fecha Inválida", "La fecha de ingreso no puede ser en el pasado.", Alert.AlertType.WARNING);
                } else if (newVal.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    dpFecha.setValue(oldVal);
                    mostrarAlerta("Día Inválido", "No se reciben mascotas los días domingo.", Alert.AlertType.WARNING);
                }
            }
        });

        // --- 4. REGLAS DEL CALENDARIO DE SALIDA (Depende de dpFecha) ---
        dpFecha.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                dpFechaSalida.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        // Para la SALIDA de la guardería, bloqueamos los días previos al ingreso
                        if (empty || date.isBefore(newVal)) {
                            setDisable(true);
                            setStyle("-fx-background-color: #f3f4f6; -fx-text-fill: #9ca3af;");
                        }
                    }
                });

                // Validación extra: Si el usuario tipea a mano una fecha de salida anterior a la entrada
                if (dpFechaSalida.getValue() != null && dpFechaSalida.getValue().isBefore(newVal)) {
                    dpFechaSalida.setValue(newVal); 
                    mostrarAlerta("Fecha Inválida", "La fecha de salida no puede ser anterior a la de ingreso.", Alert.AlertType.WARNING);
                }
            }
        });
        
        // Validación extra por si tipea a mano en dpFechaSalida directamente
        dpFechaSalida.valueProperty().addListener((obs, oldVal, newVal) -> {
             if (newVal != null && dpFecha.getValue() != null && newVal.isBefore(dpFecha.getValue())) {
                 dpFechaSalida.setValue(oldVal != null ? oldVal : dpFecha.getValue());
                 mostrarAlerta("Fecha Inválida", "La fecha de salida no puede ser anterior a la de ingreso.", Alert.AlertType.WARNING);
             }
        });
    }

    private void configurarColumnas() {
        // --- GUARDERÍA ---
        colGuarderiaMascota.setCellValueFactory(cell -> {
            String nombre = cell.getValue().getMascotaNombre();
            return new SimpleStringProperty(nombre != null && !nombre.isEmpty() ? nombre : "Sin Nombre");
        });
        colGuarderiaHora.setCellValueFactory(new PropertyValueFactory<>("horaIngreso"));
        colGuarderiaTiempo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTiempo()));
        colGuarderiaObs.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getObservaciones()));
        
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colGuarderiaIngreso.setCellValueFactory(new PropertyValueFactory<>("fechaIngreso"));
        colGuarderiaIngreso.setCellFactory(column -> new TableCell<GuarderiaDTO, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatoFecha.format(item));
                }
            }
        });

        // --- PELUQUERÍA ---
        colPeluqueriaMascota.setCellValueFactory(cell -> {
            String nombre = cell.getValue().getMascotaNombre();
            return new SimpleStringProperty(nombre != null && !nombre.isEmpty() ? nombre : "Sin Nombre");
        });
        colPeluqueriaServicio.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getServicio()));
        colPeluqueriaHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colPeluqueriaEstado.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEstado()));
        colPeluqueriaObs.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getObservaciones()));
        
        colPeluqueriaEstado.setCellFactory(column -> new TableCell<PeluqueriaDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label lbl = new Label(item);
                    lbl.setStyle("-fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 12; -fx-font-size: 11px;");
                    if (item.equalsIgnoreCase("Pendiente")) {
                        lbl.setStyle(lbl.getStyle() + "-fx-background-color: #FEF08A; -fx-text-fill: #854D0E;");
                    } else if (item.equalsIgnoreCase("Confirmado")) {
                        lbl.setStyle(lbl.getStyle() + "-fx-background-color: #BFDBFE; -fx-text-fill: #1E3A8A;");
                    } else {
                        lbl.setStyle(lbl.getStyle() + "-fx-background-color: #D1FAE5; -fx-text-fill: #065F46;");
                    }
                    setGraphic(lbl);
                    setText(null);
                }
            }
        });
    }

    private void configurarComboBoxes() {
        List<ClienteDTO> clientes = clienteService.obtenerTodos();
        cbCliente.setItems(FXCollections.observableArrayList(clientes));
        cbCliente.setConverter(new StringConverter<ClienteDTO>() {
            @Override
            public String toString(ClienteDTO cliente) {
                return (cliente != null) ? cliente.getNombre() + " " + cliente.getApellido() : "";
            }
            @Override
            public ClienteDTO fromString(String string) { return null; }
        });

        cbMascota.setConverter(new StringConverter<MascotaDTO>() {
            @Override
            public String toString(MascotaDTO mascota) {
                return (mascota != null) ? mascota.getNombreMascota() + " (" + mascota.getEspecie() + ")" : "";
            }
            @Override
            public MascotaDTO fromString(String string) { return null; }
        });

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

        ObservableList<String> horas = FXCollections.observableArrayList();
        for (int h = 8; h <= 18; h++) {
            horas.add(String.format("%02d:00", h));
            if (h != 18) horas.add(String.format("%02d:30", h));
        }
        cbHora.setItems(horas);
        
        cbTipoServicio.setConverter(new StringConverter<Object>() {
            @Override
            public String toString(Object object) {
                if (object == null) return "";
                if (object instanceof String) return (String) object; 
                if (object instanceof TipoServicio) { 
                    TipoServicio ts = (TipoServicio) object;
                    return ts.getNombreDescriptivo().replace("[PELUQUERÍA] ", "") + " ($" + ts.getPrecioBase() + ")";
                }
                return "";
            }
            @Override
            public Object fromString(String string) { return null; }
        });
    }

    private void cargarDatos() {
        List<GuarderiaDTO> guarderias = gpService.obtenerGuarderiasActivas();
        for (GuarderiaDTO g : guarderias) {
            if (g.getFechaIngreso() != null && g.getFechaIngreso().isBefore(LocalDate.now())) {
                if (!g.getEstadoTurno().equalsIgnoreCase("Atendido") && !g.getEstadoTurno().equalsIgnoreCase("Cancelado")) {
                    g.setEstadoTurno("Atendido");
                    actualizarEstadoTurnoDB(g.getIdTurno(), EstadoTurno.ATENDIDO);
                }
            }
        }
        tvGuarderia.setItems(FXCollections.observableArrayList(guarderias));
        actualizarContadorJaulas(guarderias.size());

        List<PeluqueriaDTO> peluquerias = gpService.obtenerPeluqueriasDelDia();
        for (PeluqueriaDTO p : peluquerias) {
            if (p.getFecha() != null && p.getFecha().isBefore(LocalDate.now())) {
                if (!p.getEstado().equalsIgnoreCase("Atendido") && !p.getEstado().equalsIgnoreCase("Cancelado")) {
                    p.setEstado("Atendido");
                    actualizarEstadoTurnoDB(p.getIdTurno(), EstadoTurno.ATENDIDO);
                }
            }
        }
        tvPeluqueria.setItems(FXCollections.observableArrayList(peluquerias));
    }

    private void actualizarEstadoTurnoDB(Long idTurno, EstadoTurno nuevoEstado) {
        EntityManager em = AppVeterinaria.getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            Turno t = em.find(Turno.class, idTurno);
            if (t != null) {
                t.setEstado(nuevoEstado);
                em.merge(t);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    private void actualizarContadorJaulas(int ocupadas) {
        int libres = Math.max(TOTAL_JAULAS - ocupadas, 0);
        lblCuposTotales.setText(ocupadas + "/" + TOTAL_JAULAS);
        lblJaulasOcupadas.setText("  " + ocupadas + " jaulas ocupadas");
        lblJaulasLibres.setText("  " + libres + " jaulas libres");
    }

    @FXML
    public void abrirFormularioGuarderia() {
        modoFormulario = "GUARDERIA";
        ObservableList<Object> jaulas = FXCollections.observableArrayList("Jaula A1", "Jaula A2", "Jaula B1", "Jaula B2", "Patio Abierto");
        
        boxFechaSalida.setVisible(true);
        boxFechaSalida.setManaged(true);
        boxGuarderiaExtras.setVisible(true);
        boxGuarderiaExtras.setManaged(true);
        
        txtAlimentacion.clear();
        chkActividad.setSelected(false);
        dpFechaSalida.setValue(LocalDate.now().plusDays(2));
        
        prepararFormulario("Nuevo Ingreso - Guardería", "JAULA ASIGNADA", jaulas);
        
        // BLOQUEO INTELIGENTE: Solo habilitar Guardar cuando los obligatorios estén llenos
        btnConfirmar.disableProperty().bind(
            cbCliente.valueProperty().isNull()
            .or(cbMascota.valueProperty().isNull())
            .or(dpFecha.valueProperty().isNull())
            .or(dpFechaSalida.valueProperty().isNull())
            .or(cbHora.valueProperty().isNull())
            .or(cbTipoServicio.valueProperty().isNull())
        );
    }

    @FXML
    public void abrirFormularioPeluqueria() {
        modoFormulario = "PELUQUERIA";
        
        List<TipoServicio> serviciosBD = tipoServicioService.obtenerTodos().stream()
                .filter(ts -> ts.getNombreDescriptivo().startsWith("[PELUQUERÍA] "))
                .collect(Collectors.toList());
        ObservableList<Object> cortes = FXCollections.observableArrayList(serviciosBD);
        
        boxFechaSalida.setVisible(false);
        boxFechaSalida.setManaged(false);
        boxGuarderiaExtras.setVisible(false);
        boxGuarderiaExtras.setManaged(false);
        
        prepararFormulario("Nuevo Turno - Peluquería", "TIPO DE CORTE / SERVICIO", cortes);
        
        // BLOQUEO INTELIGENTE: (Igual que el anterior pero sin la fecha de salida, porque peluquería no lo usa)
        btnConfirmar.disableProperty().bind(
            cbCliente.valueProperty().isNull()
            .or(cbMascota.valueProperty().isNull())
            .or(dpFecha.valueProperty().isNull())
            .or(cbHora.valueProperty().isNull())
            .or(cbTipoServicio.valueProperty().isNull())
        );
    }

    private void prepararFormulario(String titulo, String labelServicio, ObservableList<Object> opciones) {
        lblTituloFormulario.setText(titulo);
        lblTipoServicio.setText(labelServicio);
        cbTipoServicio.setItems(opciones);
        
        cbCliente.getSelectionModel().clearSelection();
        cbMascota.getSelectionModel().clearSelection();
        cbMascota.setDisable(true);
        cbTipoServicio.getSelectionModel().clearSelection();
        
        // Adelantar hasta un día hábil si mañana es domingo
        LocalDate nextDay = LocalDate.now().plusDays(1);
        if (nextDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
            nextDay = nextDay.plusDays(1);
        }
        dpFecha.setValue(nextDay);
        
        cbHora.getSelectionModel().clearSelection();
        txtObservaciones.clear();
        
        overlayFormulario.setVisible(true);
    }

@FXML
    private void guardarRegistro() {
        // 1. Validaciones iniciales
        if (cbCliente.getValue() == null || cbMascota.getValue() == null || dpFecha.getValue() == null || cbHora.getValue() == null || cbTipoServicio.getValue() == null) {
            mostrarAlerta("Campos Incompletos", "Complete todos los campos obligatorios.", javafx.scene.control.Alert.AlertType.WARNING);
            return;
        }

        boolean esGuarderia = cbTipoServicio.getValue() instanceof String;
        if (esGuarderia && dpFechaSalida.getValue() == null) {
            mostrarAlerta("Campos Incompletos", "Debe indicar la fecha de salida para la guardería.", javafx.scene.control.Alert.AlertType.WARNING);
            return;
        }

        Long idCliente = cbCliente.getValue().getId();
        Long idMascota = cbMascota.getValue().getId();
        java.time.LocalDate fecha = dpFecha.getValue();
        java.time.LocalDate salida = dpFechaSalida.getValue();
        String horaStr = cbHora.getValue();
        java.time.LocalTime horaParsed;

        try {
            horaParsed = java.time.LocalTime.parse(horaStr);
        } catch (Exception e) {
            mostrarAlerta("Hora Inválida", "La hora ingresada no es válida.", javafx.scene.control.Alert.AlertType.WARNING);
            return;
        }

        if (esGuarderia && salida.isBefore(fecha)) {
            mostrarAlerta("Atención", "La fecha de salida debe ser posterior a la de ingreso.", javafx.scene.control.Alert.AlertType.WARNING);
            return;
        }

        String observaciones = txtObservaciones.getText() != null ? txtObservaciones.getText() : "";

        // 2. Bloque Try-Catch Defensivo
        try {
            if (esGuarderia) {
                String jaula = cbTipoServicio.getValue().toString();
                String alimentacion = txtAlimentacion.getText() != null ? txtAlimentacion.getText().trim() : "";
                boolean actividad = chkActividad.isSelected();
                
                gpService.registrarGuarderia(idCliente, idMascota, fecha, horaParsed, salida, jaula, alimentacion, actividad, observaciones);
            } else {
                ar.edu.unam.veterinaria.model.TipoServicio servicioElegido = (ar.edu.unam.veterinaria.model.TipoServicio) cbTipoServicio.getValue();
                
                gpService.registrarPeluqueria(idCliente, idMascota, fecha, horaParsed, servicioElegido, observaciones);
            }
            
            mostrarAlerta("Éxito", "Registro guardado correctamente.", javafx.scene.control.Alert.AlertType.INFORMATION);
            cerrarModales();
            cargarDatos();
            
        } catch (ar.edu.unam.veterinaria.exception.CupoLLeno | ar.edu.unam.veterinaria.exception.JaulaNoDisponible e) {
            // Atrapamos las excepciones de nuestro Modelo de Dominio
            mostrarAlerta("Límite Alcanzado", e.getMessage(), javafx.scene.control.Alert.AlertType.WARNING);
            
        } catch (Exception e) {
            // Atrapamos cualquier otro error (ej. caída de base de datos)
            mostrarAlerta("Error", "Error al guardar en la base de datos.", javafx.scene.control.Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void seleccionarMascotaGuarderia() {
        GuarderiaDTO dto = tvGuarderia.getSelectionModel().getSelectedItem();
        if (dto != null) {
            idTurnoSeleccionado = dto.getIdTurno();
            
            lblDetalleNombre.setText(dto.getMascotaNombre() != null ? dto.getMascotaNombre() : "Sin nombre");
            lblDetalleDueno.setText("Dueño: " + dto.getClienteNombre());
            lblDetalleEspecie.setText(dto.getEspecie());
            lblDetalleRaza.setText(dto.getRaza());
            
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            lblDetalleEdad.setText(dto.getFechaNacimiento() != null ? dto.getFechaNacimiento().format(fmt) : "Desconocida"); 
            lblDetalleFicha.setText(dto.getNumeroFicha() != null ? String.valueOf(dto.getNumeroFicha()) : "Sin Ficha");
            
            lblDetalleTurnoFecha.setText(dto.getFechaIngreso() != null ? dto.getFechaIngreso().format(fmt) : "-");
            lblDetalleTurnoHora.setText(dto.getHoraIngreso() != null ? dto.getHoraIngreso().toString() : "Sin especificar"); 
            
            lblDetallePrecio.setText(String.format("$ %.2f", dto.getCostoTotal() != null ? dto.getCostoTotal() : 0.0));
            
            boxDetallesExtras.setVisible(true);
            boxDetallesExtras.setManaged(true);
            lblDetalleActividad.setText(dto.isRequiereActividad() ? "Sí, requiere paseos" : "No solicitada");
            
            boxDetallesAlimentacion.setVisible(true);
            boxDetallesAlimentacion.setManaged(true);
            lblDetalleAlimentacion.setText(dto.getAlimentacionEspecifica() != null && !dto.getAlimentacionEspecifica().isEmpty() ? dto.getAlimentacionEspecifica() : "Dieta estándar del centro.");

            lblDetalleServicioEstado.setText("Jaula: " + dto.getJaula() + "\nEstado: " + dto.getEstadoTurno() + " (" + dto.getTiempo() + ")");
            lblDetalleObservaciones.setText(dto.getObservaciones() != null && !dto.getObservaciones().isEmpty() ? dto.getObservaciones() : "Sin observaciones.");
            
            btnConfirmar.setVisible(false); btnConfirmar.setManaged(false);
            btnCancelar.setVisible(false);  btnCancelar.setManaged(false);
            btnEliminar.setDisable(false);
            
            overlayDetalles.setVisible(true);
        }
    }

    @FXML
    public void seleccionarMascotaPeluqueria() {
        PeluqueriaDTO dto = tvPeluqueria.getSelectionModel().getSelectedItem();
        if (dto != null) {
            idTurnoSeleccionado = dto.getIdTurno();
            
            lblDetalleNombre.setText(dto.getMascotaNombre() != null ? dto.getMascotaNombre() : "Sin nombre");
            lblDetalleDueno.setText("Dueño: " + dto.getClienteNombre());
            lblDetalleEspecie.setText(dto.getEspecie());
            lblDetalleRaza.setText(dto.getRaza());
            
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            lblDetalleEdad.setText(dto.getFechaNacimiento() != null ? dto.getFechaNacimiento().format(fmt) : "Desconocida");
            lblDetalleFicha.setText(dto.getNumeroFicha() != null ? String.valueOf(dto.getNumeroFicha()) : "Sin Ficha");
            
            lblDetalleTurnoFecha.setText(dto.getFecha() != null ? dto.getFecha().format(fmt) : "-");
            lblDetalleTurnoHora.setText(dto.getHora() != null ? dto.getHora().toString() : "-");
            
            lblDetallePrecio.setText(String.format("$ %.2f", dto.getCostoTotal() != null ? dto.getCostoTotal() : 0.0));
            
            boxDetallesExtras.setVisible(false);
            boxDetallesExtras.setManaged(false);
            boxDetallesAlimentacion.setVisible(false);
            boxDetallesAlimentacion.setManaged(false);
            
            lblDetalleServicioEstado.setText("Corte: " + dto.getServicio() + "\nEstado: " + dto.getEstado());
            lblDetalleObservaciones.setText(dto.getObservaciones() != null && !dto.getObservaciones().isEmpty() ? dto.getObservaciones() : "Sin observaciones.");
            
            btnConfirmar.setVisible(true); btnConfirmar.setManaged(true);
            btnCancelar.setVisible(true);  btnCancelar.setManaged(true);
            
            configurarBotonesPeluqueria(dto.getEstado());
            overlayDetalles.setVisible(true);
        }
    }

    private void configurarBotonesPeluqueria(String estado) {
        btnConfirmar.setDisable(estado.equalsIgnoreCase("CONFIRMADO") || estado.equalsIgnoreCase("CANCELADO") || estado.equalsIgnoreCase("ATENDIDO"));
        btnCancelar.setDisable(estado.equalsIgnoreCase("CANCELADO") || estado.equalsIgnoreCase("ATENDIDO"));
        btnEliminar.setDisable(!(estado.equalsIgnoreCase("CANCELADO") || estado.equalsIgnoreCase("ATENDIDO")));
    }

    @FXML
    public void confirmarTurno() {
        if (idTurnoSeleccionado != null) {
            try {
                turnoService.confirmarTurno(idTurnoSeleccionado);
                mostrarAlerta("Éxito", "Turno confirmado correctamente.", Alert.AlertType.INFORMATION);
                cerrarModales();
                cargarDatos();
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo confirmar el turno.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void cancelarTurno() {
        if (idTurnoSeleccionado != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Cancelar Turno");
            confirm.setHeaderText(null);
            confirm.setContentText("¿Está seguro que desea CANCELAR este turno? (No podrá confirmarlo nuevamente)");
            
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                try {
                    turnoService.cancelarTurno(idTurnoSeleccionado);
                    mostrarAlerta("Éxito", "El turno ha sido cancelado.", Alert.AlertType.INFORMATION);
                    cerrarModales();
                    cargarDatos();
                } catch (CancelacionFueradeTermino e) {
                    mostrarAlerta("Atención", e.getMessage(), Alert.AlertType.WARNING);
                } catch (Exception e) {
                    mostrarAlerta("Error", "No se pudo cancelar el turno.", Alert.AlertType.ERROR);
                }
            }
        }
    }

    @FXML
    public void darDeBajaRegistro() {
        if (idTurnoSeleccionado != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Eliminar Registro");
            confirm.setHeaderText(null);
            confirm.setContentText("¿Está seguro que desea ELIMINAR definitivamente este registro de la base de datos?");
            
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                EntityManager em = AppVeterinaria.getEmf().createEntityManager();
                try {
                    em.getTransaction().begin();
                    Turno t = em.find(Turno.class, idTurnoSeleccionado);
                    if (t != null) {
                        em.remove(t); 
                    }
                    em.getTransaction().commit();
                    mostrarAlerta("Éxito", "Registro eliminado del sistema.", Alert.AlertType.INFORMATION);
                    cerrarModales();
                    cargarDatos();
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) em.getTransaction().rollback();
                    mostrarAlerta("Error", "No se pudo eliminar el registro.", Alert.AlertType.ERROR);
                } finally {
                    em.close();
                }
            }
        }
    }

    @FXML
    public void cerrarModales() {
        overlayFormulario.setVisible(false);
        overlayDetalles.setVisible(false);
        tvGuarderia.getSelectionModel().clearSelection();
        tvPeluqueria.getSelectionModel().clearSelection();
        idTurnoSeleccionado = null;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}