package br.com.fiap.petfiap.exception;

// Lancada quando o atendimento nao existe no banco.
public class AtendimentoNaoEncontradoException extends RuntimeException {

    public AtendimentoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
