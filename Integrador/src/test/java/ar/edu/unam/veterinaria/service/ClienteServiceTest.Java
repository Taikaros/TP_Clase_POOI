package ar.edu.unam.veterinaria.service;

import ar.edu.unam.veterinaria.dto.ClienteDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClienteServiceTest {

    @Test
    public void testGuardarYListarCliente() {
        ClienteService service = new ClienteService();

        // Creamos un DTO de prueba
        ClienteDTO dto = new ClienteDTO(null, "TestNombre", "TestApellido", "11112222", "test@email.com");
        
        // Ejecutamos el servicio de guardado
        ClienteDTO resultado = service.guardarCliente(dto);

        // Verificaciones (Assertions)
        assertNotNull(resultado, "El servicio debería devolver el DTO procesado");
        assertNotNull(resultado.getId(), "La base de datos debió generar un ID automático");
        assertEquals("TestNombre", resultado.getNombre(), "El nombre debe coincidir");
    }
}