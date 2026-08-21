package ar.edu.unam.veterinaria.controller;

import ar.edu.unam.veterinaria.dto.ClienteDTO;
import ar.edu.unam.veterinaria.dto.MascotaDTO;
import ar.edu.unam.veterinaria.dto.VeterinarioDTO;
import ar.edu.unam.veterinaria.service.ClienteService;
import ar.edu.unam.veterinaria.service.MascotaService;
import ar.edu.unam.veterinaria.service.VeterinarioService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.util.List;
import java.util.stream.Collectors;

public class TurnoController {

    // --- Servicios de Base de Datos ---
    private ClienteService clienteService = new ClienteService();
    private MascotaService mascotaService = new MascotaService();
    private VeterinarioService veterinarioService = new VeterinarioService();
    // private TurnoService turnoService = new TurnoService(); // Descomentar cuando exista

    // --- Componentes UI de los Contadores ---
    @FXML private Label lblTurnosActivos;
    @FXML private Label lblCountPendiente;
    @FXML private Label lblCountAsistio;
    @FXML private Label lblCountCancelado;

    // --- Componentes UI del Formulario ---
    @FXML private ComboBox<ClienteDTO> cbCliente;
    @FXML private ComboBox<MascotaDTO> cbMascota;
    @FXML private ComboBox<VeterinarioDTO> cbVeterinario;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtHora;
    @FXML private TextArea txtNotas;

    // --- Componentes UI de la Tabla ---
    // NOTA: Cuando crees el TurnoDTO, reemplaza los '?' por 'TurnoDTO' y los tipos correspondientes
    @FXML private TableView<?> tvTurnos; 
    @FXML private TableColumn<?, ?> colHora;
    @FXML private TableColumn<?, ?> colMascota;
    @FXML private TableColumn<?, ?> colVeterinario;
    @FXML private TableColumn<?, ?> colServicios;
    @FXML private TableColumn<?, ?> colEstado;

    @FXML
    public void initialize() {
        configurarConvertidoresComboBox();
        cargarDatosDesdeBD();
        configurarFiltroMascotas();
        
        // Cargar tabla (debes implementarlo cuando tengas TurnoDTO listo)
        // cargarTabla(); 
    }

    private void cargarDatosDesdeBD() {
        // Cargar Clientes
        List<ClienteDTO> clientes = clienteService.obtenerTodos();
        cbCliente.setItems(FXCollections.observableArrayList(clientes));

        // Cargar Veterinarios
        List<VeterinarioDTO> veterinarios = veterinarioService.obtenerTodos();
        cbVeterinario.setItems(FXCollections.observableArrayList(veterinarios));
    }

    private void configurarFiltroMascotas() {
        // Escuchar cuando el usuario selecciona un cliente
        cbCliente.getSelectionModel().selectedItemProperty().addListener((observable, viejoCliente, nuevoCliente) -> {
            if (nuevoCliente != null) {
                // Obtener todas las mascotas y filtrar por el ID del cliente seleccionado
                List<MascotaDTO> mascotasDelCliente = mascotaService.obtenerTodas().stream()
                        .filter(m -> m.getIdCliente() != null && m.getIdCliente().equals(nuevoCliente.getId()))
                        .collect(Collectors.toList());

                // Actualizar el ComboBox de mascotas y habilitarlo
                cbMascota.setItems(FXCollections.observableArrayList(mascotasDelCliente));
                cbMascota.setDisable(false);
            } else {
                // Si no hay cliente seleccionado, vaciar y deshabilitar mascotas
                cbMascota.getItems().clear();
                cbMascota.setDisable(true);
            }
        });
    }

    private void configurarConvertidoresComboBox() {
        // Enseña a JavaFX cómo mostrar los DTOs en el texto de las listas desplegables
        
        cbCliente.setConverter(new StringConverter<ClienteDTO>() {
            @Override
            public String toString(ClienteDTO cliente) {
                return cliente == null ? "" : cliente.getNombre() + " " + cliente.getApellido();
            }
            @Override
            public ClienteDTO fromString(String string) { return null; }
        });

        cbMascota.setConverter(new StringConverter<MascotaDTO>() {
            @Override
            public String toString(MascotaDTO mascota) {
                return mascota == null ? "" : mascota.getNombreMascota() + " (" + mascota.getEspecie() + ")";
            }
            @Override
            public MascotaDTO fromString(String string) { return null; }
        });

        cbVeterinario.setConverter(new StringConverter<VeterinarioDTO>() {
            @Override
            public String toString(VeterinarioDTO vet) {
                return vet == null ? "" : "Dr/a. " + vet.getNombre() + " " + vet.getApellido();
            }
            @Override
            public VeterinarioDTO fromString(String string) { return null; }
        });
    }

    @FXML
    public void guardarTurno(ActionEvent event) {
        // Validaciones básicas
        if (cbMascota.getValue() == null || cbVeterinario.getValue() == null || dpFecha.getValue() == null || txtHora.getText().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Por favor, complete todos los datos obligatorios para confirmar el turno.", Alert.AlertType.WARNING);
            return;
        }

        // Aquí iría la lógica para instanciar el DTO y enviarlo a la BD
        System.out.println("Turno confirmado para: " + cbMascota.getValue().getNombreMascota() + " con " + cbVeterinario.getValue().getApellido());
        
        mostrarAlerta("Éxito", "El turno ha sido agendado correctamente.", Alert.AlertType.INFORMATION);
        limpiarFormulario(null);
        // cargarTabla(); // Refrescar la tabla al guardar
    }

    @FXML
    public void limpiarFormulario(ActionEvent event) {
        cbCliente.getSelectionModel().clearSelection();
        cbMascota.getSelectionModel().clearSelection();
        cbMascota.setDisable(true);
        cbVeterinario.getSelectionModel().clearSelection();
        dpFecha.setValue(null);
        txtHora.clear();
        txtNotas.clear();
    }

    /**
     * NOTA: Debes llamar a este método cuando cargues la tabla y le pasas la lista de TurnoDTO
     * para que los números cambien dinámicamente según la base de datos.
     */
    /* DESCOMENTAR CUANDO TENGAS LA LISTA DE TURNOS LISTA
    private void actualizarContadores(List<TurnoDTO> listaTurnos) {
        if (listaTurnos == null) return;

        long pendientes = 0;
        long asistieron = 0;
        long cancelados = 0;

        for (TurnoDTO turno : listaTurnos) {
            // Aseguramos de que coincida con el nombre del Enum EstadoTurno
            String estado = turno.getEstado().toString().toUpperCase(); 
            
            switch (estado) {
                case "PENDIENTE":
                    pendientes++;
                    break;
                case "ASISTIO":
                    asistieron++;
                    break;
                case "CANCELADO":
                    cancelados++;
                    break;
            }
        }

        lblCountPendiente.setText(String.valueOf(pendientes));
        lblCountAsistio.setText(String.valueOf(asistieron));
        lblCountCancelado.setText(String.valueOf(cancelados));
        
        lblTurnosActivos.setText(listaTurnos.size() + " turnos en total");
    }
    */

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}