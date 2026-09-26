package br.com.fiap.petfiap.builder;

import br.com.fiap.petfiap.model.Atendimento;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Testes unitarios do Builder (Aula 14 + Aula 15): sem banco, sem Spring.
public class AtendimentoBuilderTest {

    private LocalDateTime data = LocalDateTime.of(2026, 10, 1, 10, 0);

    @Test
    public void deveMontarAtendimentoCompleto() {
        // Act
        Atendimento atendimento = new AtendimentoBuilder()
                .comTipo("BANHO")
                .comPet("Rex", "PEQUENO")
                .comTutor("Ana")
                .comDataHora(data)
                .construir(7);

        // Assert: o objeto montado carrega tudo o que foi configurado
        assertEquals("BANHO", atendimento.getTipo());
        assertEquals("Rex", atendimento.getPetNome());
        assertEquals("PEQUENO", atendimento.getPetPorte());
        assertEquals("Ana", atendimento.getTutorNome());
    }

    @Test
    public void deveRecusarMontagemSemNomeDoPet() {
        // Act + Assert: o objeto so nasce valido (validacao concentrada no construir)
        assertThrows(IllegalArgumentException.class, () -> new AtendimentoBuilder()
                .comTipo("BANHO")
                .comPet(null, "PEQUENO")
                .comTutor("Ana")
                .comDataHora(data)
                .construir(8));
    }

    @Test
    public void deveRecusarMontagemSemPorte() {
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> new AtendimentoBuilder()
                .comTipo("TOSA")
                .comPet("Rex", null)
                .comTutor("Ana")
                .comDataHora(data)
                .construir(9));
    }
}
