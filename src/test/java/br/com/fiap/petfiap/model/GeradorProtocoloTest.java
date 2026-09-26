package br.com.fiap.petfiap.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

// Testes unitarios do Singleton (Aula 14 + Aula 15): sem banco, sem Spring.
public class GeradorProtocoloTest {

    @Test
    public void deveManterUmaUnicaInstancia() {
        // Arrange + Act
        GeradorProtocolo primeira = GeradorProtocolo.getInstancia();
        GeradorProtocolo segunda = GeradorProtocolo.getInstancia();

        // Assert: e sempre o MESMO objeto (padrao Singleton)
        assertSame(primeira, segunda);
    }

    @Test
    public void deveGerarProtocolosSequenciais() {
        // Act: a numeracao e global - cada atendimento novo pega o proximo numero,
        // independente de quantas vezes o sistema pede o gerador
        int primeiro = GeradorProtocolo.getInstancia().proximo();
        int segundo = GeradorProtocolo.getInstancia().proximo();
        int terceiro = GeradorProtocolo.getInstancia().proximo();

        // Assert: 1, 2, 3... (numeracao sequencial dos atendimentos)
        assertEquals(1, primeiro);
        assertEquals(2, segundo);
        assertEquals(3, terceiro);
    }
}
