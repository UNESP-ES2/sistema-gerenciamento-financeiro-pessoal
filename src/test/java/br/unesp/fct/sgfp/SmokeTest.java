package br.unesp.fct.sgfp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SmokeTest {

    @Test
    @DisplayName("Teste de Fumaça para validar o JUnit 5")
    void smokeTestJUnit() {
        assertTrue(true, "Tudo funcionando corretamente!");
    }
}
