package br.com.fiap.petfiap.factory;

import br.com.fiap.petfiap.model.Atendimento;
import br.com.fiap.petfiap.model.Banho;
import br.com.fiap.petfiap.model.ConsultaVeterinaria;
import br.com.fiap.petfiap.model.Tosa;

import java.time.LocalDateTime;

// Padrao Factory (Aula 14): o unico lugar que conhece as subclasses concretas.
// O resto do codigo depende apenas do tipo abstrato Atendimento.
public class AtendimentoFactory {

    public static Atendimento criar(int p, String t, String n, String po, String tu, LocalDateTime d) {
        return switch (t) {
            case "BANHO" -> new Banho(p, n, po, tu, d);
            case "TOSA" -> new Tosa(p, n, po, tu, d);
            case "CONSULTA" -> new ConsultaVeterinaria(p, n, po, tu, d);
            default -> throw new IllegalArgumentException("Tipo invalido: " + t);
        };
    }
}
