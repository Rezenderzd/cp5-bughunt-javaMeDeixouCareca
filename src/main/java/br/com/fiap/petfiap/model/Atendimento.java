package br.com.fiap.petfiap.model;

import br.com.fiap.petfiap.exception.StatusInvalidoException;
import jakarta.persistence.*;

import java.time.LocalDateTime;

// Atendimento do PetFiap: banho, tosa ou consulta veterinaria.
// As regras de preco, pontos e duracao moram nas subclasses (polimorfismo).
@Entity
@Table(name = "atendimentos")
public abstract class Atendimento {

    public static final String AGENDADO = "AGENDADO";
    public static final String CONCLUIDO = "CONCLUIDO";
    public static final String CANCELADO = "CANCELADO";

    public static final String PEQUENO = "PEQUENO";
    public static final String MEDIO = "MEDIO";
    public static final String GRANDE = "GRANDE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int protocolo;

    private String petNome;
    private String petPorte;
    private String tutorNome;

    private LocalDateTime dataHora;

    // AGENDADO, CONCLUIDO ou CANCELADO
    private String status;

    protected Atendimento() {
    }

    protected Atendimento(int protocolo, String petNome, String petPorte, String tutorNome, LocalDateTime dataHora) {
        this.protocolo = protocolo;
        this.petNome = petNome;
        this.petPorte = petPorte;
        this.tutorNome = tutorNome;
        this.dataHora = dataHora;
        this.status = AGENDADO;
    }

    // tipo do atendimento (BANHO, TOSA, CONSULTA)
    public abstract String getTipo();

    // preco do atendimento segundo o porte do pet
    public abstract double calcularPreco();

    // pontos de fidelidade acumulados pelo tutor
    public abstract int calcularPontosFidelidade();

    // duracao media em minutos; subclasses mais demoradas sobrescrevem
    public int getDuracaoMinutos() {
        return 30;
    }

    // Conclui o atendimento (so pode em AGENDADO)
    public void concluir() {
        if (!AGENDADO.equals(status)) {
            throw new StatusInvalidoException("Atendimento " + protocolo + " nao pode ser concluido: status " + status);
        }
        status = CONCLUIDO;
    }

    // Cancela o atendimento
    public void cancelar() {
        if (!AGENDADO.equals(status)) {
            throw new StatusInvalidoException("Atendimento " + protocolo + " nao pode ser cancelado: status " + status);
        }
        status = CANCELADO;
    }

    public Long getId() { return id; }

    public int getProtocolo() { return protocolo; }

    public String getPetNome() { return petNome; }

    public String getPetPorte() { return petPorte; }

    public String getTutorNome() { return tutorNome; }

    public LocalDateTime getDataHora() { return dataHora; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
