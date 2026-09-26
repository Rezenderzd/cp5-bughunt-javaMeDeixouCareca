package br.com.fiap.petfiap.model;

import jakarta.persistence.Entity;

import java.time.LocalDateTime;

// Banho: preco por porte, 20 pontos, 45 minutos.
@Entity
public class Banho extends Atendimento {

    public static final String TIPO = "BANHO";

    public Banho() {
    }

    public Banho(int protocolo, String petNome, String petPorte, String tutorNome, LocalDateTime dataHora) {
        super(protocolo, petNome, petPorte, tutorNome, dataHora);
    }

    @Override
    public String getTipo() {
        return TIPO;
    }

    @Override
    public double calcularPreco() {
        if ("PEQUENO".equals(getPetPorte())) {
            return 100.0;
        } else if ("MEDIO".equals(getPetPorte())) {
            return 80.0;
        }
        return 60.0;
    }

    @Override
    public int calcularPontosFidelidade() {
        return 20;
    }

    @Override
    public int getDuracaoMinutos() {
        return 45;
    }
}
