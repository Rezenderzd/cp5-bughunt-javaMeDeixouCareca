package br.com.fiap.petfiap.factory;

import br.com.fiap.petfiap.model.Atendimento;
import br.com.fiap.petfiap.model.Banho;
import br.com.fiap.petfiap.model.ConsultaVeterinaria;
import br.com.fiap.petfiap.model.Tosa;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Testes unitarios da Factory (Aula 14 + Aula 15): sem banco, sem Spring.
public class AtendimentoFactoryTest {

    private LocalDateTime data = LocalDateTime.of(2026, 10, 1, 10, 0);

    @Test
    public void deveCriarBanhoQuandoTipoForBanho() {
        // Act
        Atendimento atendimento = AtendimentoFactory.criar(1, "BANHO", "Rex", "PEQUENO", "Ana", data);

        // Assert
        assertInstanceOf(Banho.class, atendimento);
    }

    @Test
    public void deveCriarTosaQuandoTipoForTosa() {
        // Act
        Atendimento atendimento = AtendimentoFactory.criar(2, "TOSA", "Rex", "PEQUENO", "Ana", data);

        // Assert
        assertInstanceOf(Tosa.class, atendimento);
    }

    @Test
    public void devePreencherOsDadosDoPetNaConsulta() {
        // Act
        Atendimento atendimento = AtendimentoFactory.criar(
                3, "CONSULTA", "Mimi", "MEDIO", "Bruno", data.plusDays(1));

        // Assert: os dados passados chegam ao objeto criado
        assertEquals("Mimi", atendimento.getPetNome());
        assertEquals("MEDIO", atendimento.getPetPorte());
        assertEquals("Bruno", atendimento.getTutorNome());
    }

    @Test
    public void deveRecusarTipoInexistente() {
        // Act + Assert
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> AtendimentoFactory.criar(4, "VACINA", "Rex", "PEQUENO", "Ana", data));

        assertEquals("Tipo invalido: VACINA", excecao.getMessage());
    }
}
