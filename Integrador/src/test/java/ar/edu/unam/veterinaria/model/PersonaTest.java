package ar.edu.unam.veterinaria.model;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PersonaTest {

    @Test
    public void testGetContactoFormateoCorrecto() {
        // Arrange (Preparar): Instanciamos Persona usando una clase anónima agregando "{}" al final
        Persona personaDummy = new Persona("Juan", "Pérez", "3758-123456", "juan@email.com") {};

        // Act (Actuar): Obtenemos el resultado del método
        String resultado = personaDummy.getTelefono() + ", " + personaDummy.getEmail();

        // Assert (Afirmar): Verificamos que el formato sea exactamente el esperado
        String esperado = "3758-123456, juan@email.com";
        assertEquals("El formato del contacto no coincide", esperado, resultado);
    }

    @Test
    public void testSetDatosPersonalesActualizaCorrectamente() {
        // Arrange
        Persona personaDummy = new Persona() {};

        // Act
        personaDummy.setNombre("Carlos");
        personaDummy.setApellido("Gómez");

        // Assert
        assertEquals("El nombre no se actualizó correctamente", "Carlos", personaDummy.getNombre());
        assertEquals("El apellido no se actualizó correctamente", "Gómez", personaDummy.getApellido());
    }
}