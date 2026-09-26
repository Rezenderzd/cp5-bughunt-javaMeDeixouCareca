package br.com.fiap.petfiap.service;

import br.com.fiap.petfiap.exception.AtendimentoNaoEncontradoException;
import br.com.fiap.petfiap.exception.HorarioOcupadoException;
import br.com.fiap.petfiap.exception.StatusInvalidoException;
import br.com.fiap.petfiap.model.Atendimento;
import br.com.fiap.petfiap.model.Banho;
import br.com.fiap.petfiap.repository.AtendimentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Teste unitario da agenda: NAO sobe o Spring e NAO conecta no Oracle (Aula 15).
// O @Mock cria um AtendimentoRepository falso; o @InjectMocks injeta esse falso
// no AgendaService - no teste, quem faz o trabalho do @Autowired e o Mockito.
@ExtendWith(MockitoExtension.class)
public class AgendaServiceTest {

    @Mock
    private AtendimentoRepository repository;

    @InjectMocks
    private AgendaService service;

    private Banho banhoDoRexAmanha10h() {
        return new Banho(1, "Rex", "PEQUENO", "Ana", LocalDateTime.now().plusDays(1).withNano(0));
    }

    @Test
    public void deveAgendarQuandoHorarioDoPetEstaLivre() {
        // Arrange: nenhum atendimento anterior para o Rex
        Banho banho = banhoDoRexAmanha10h();
        when(repository.findByPetNome("Rex")).thenReturn(List.of());
        when(repository.save(banho)).thenReturn(banho);

        // Act
        Atendimento salvo = service.agendar(banho);

        // Assert
        assertEquals("Rex", salvo.getPetNome());
        assertEquals("AGENDADO", salvo.getStatus());
        verify(repository).save(banho);
    }

    @Test
    public void deveRecusarAgendamentoComHorarioJaOcupado() {
        // Arrange: o Rex ja tem banho AGENDADO amanha as 10h
        Banho existente = banhoDoRexAmanha10h();
        when(repository.findByPetNome("Rex")).thenReturn(List.of(existente));

        // A MESMA data/hora em outro objeto: e o que acontece no mundo real,
        // quando duas requisicoes diferentes trazem valores iguais
        LocalDateTime mesmoHorarioEmOutroObjeto = LocalDateTime.parse(existente.getDataHora().toString());
        Banho novaTentativa = new Banho(2, "Rex", "PEQUENO", "Ana", mesmoHorarioEmOutroObjeto);

        // Act + Assert
        assertThrows(HorarioOcupadoException.class, () -> service.agendar(novaTentativa));

        // O banco NUNCA e acionado com o conflito detectado
        verify(repository, never()).save(any());
    }

    @Test
    public void deveConcluirAtendimentoAgendado() {
        // Arrange
        Banho agendado = banhoDoRexAmanha10h();
        when(repository.findById(1L)).thenReturn(Optional.of(agendado));
        when(repository.save(agendado)).thenReturn(agendado);

        // Act
        Atendimento concluido = service.concluir(1L);

        // Assert
        assertEquals("CONCLUIDO", concluido.getStatus());
        verify(repository).save(agendado);
    }

    @Test
    public void deveRecusarConclusaoDeAtendimentoJaConcluido() {
        // Arrange
        Banho jaConcluido = banhoDoRexAmanha10h();
        jaConcluido.setStatus("CONCLUIDO");
        when(repository.findById(1L)).thenReturn(Optional.of(jaConcluido));

        // Act + Assert: a excecao ESPERADA
        assertThrows(StatusInvalidoException.class, () -> service.concluir(1L));

        // Nada e salvo quando a operacao e recusada
        verify(repository, never()).save(any());
    }

    @Test
    public void deveLancarExcecaoQuandoAtendimentoNaoExiste() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(AtendimentoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }
}
