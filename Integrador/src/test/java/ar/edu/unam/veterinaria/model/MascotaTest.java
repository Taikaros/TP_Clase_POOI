package ar.edu.unam.veterinaria.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

 class MascotaTest {

    @Test
    void noDebePermitirCrearMascotaSinCliente() {

        assertThrows(
            IllegalArgumentException.class,
            () -> new Mascota(
                "Firulais",
                "Perro",
                "Labrador",
                LocalDate.of(2020, 5, 10),
                (Cliente) null,
                13548754550L
            )
        );

    }
}