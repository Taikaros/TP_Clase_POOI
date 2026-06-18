package ar.edu.unam.veterinaria.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonaTest {

    @Test
    void testGetContactoFormateoCorrecto() {
        // Arrange (Preparar): Instanciamos Persona usando una clase anónima agregando "{}" al final
        Persona personaDummy = new Persona("Juan", "Pérez", "3758-123456", "juan@email.com") {};

        // Act (Actuar): Obtenemos el resultado del método
        String resultado = personaDummy.getContacto();

        // Assert (Afirmar): Verificamos que el formato sea exactamente el esperado
        String esperado = "Teléfono: 3758-123456, Email: juan@email.com";
        assertEquals(esperado, resultado, "El formato del contacto no coincide");
    }

    @Test
    void testSetDatosPersonalesActualizaCorrectamente() {
        // Arrange
        Persona personaDummy = new Persona() {};
        
        // Act
        personaDummy.setDatosPersonales("Carlos", "Gómez");

        // Assert
        assertEquals("Carlos", personaDummy.getNombre(), "El nombre no se actualizó correctamente");
        assertEquals("Gómez", personaDummy.getApellido(), "El apellido no se actualizó correctamente");
    }
}