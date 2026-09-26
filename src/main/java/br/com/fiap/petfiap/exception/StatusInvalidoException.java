package br.com.fiap.petfiap.exception;

// Lancada quando o status do atendimento nao permite a operacao pedida
// (por exemplo, concluir ou cancelar um atendimento ja concluido).
public class StatusInvalidoException extends RuntimeException {

    public StatusInvalidoException(String mensagem) {
        super(mensagem);
    }
}
