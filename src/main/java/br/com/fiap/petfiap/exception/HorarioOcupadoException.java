package br.com.fiap.petfiap.exception;

// Lancada quando o pet ja tem um atendimento AGENDADO no mesmo horario.
public class HorarioOcupadoException extends RuntimeException {

    public HorarioOcupadoException(String mensagem) {
        super(mensagem);
    }
}
