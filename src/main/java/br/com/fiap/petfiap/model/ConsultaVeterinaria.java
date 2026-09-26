package br.com.fiap.petfiap.model;

import jakarta.persistence.Entity;

import java.time.LocalDateTime;

// Consulta veterinaria: preco fixo (independe do porte), 50 pontos, 30 minutos.
@Entity
public class ConsultaVeterinaria extends Atendimento {

    public static final String TIPO = "CONSULTA";

    public ConsultaVeterinaria() {
    }

    public ConsultaVeterinaria(int protocolo, String petNome, String petPorte, String tutorNome, LocalDateTime dataHora) {
        super();
    }

    @Override
    public String getTipo() {
        return TIPO;
    }

    @Override
    public double calcularPreco() {
        return 150.0;
    }

    @Override
    public int calcularPontosFidelidade() {
        return 50;
    }
}
