package br.com.fiap.petfiap.service;

import br.com.fiap.petfiap.exception.AtendimentoNaoEncontradoException;
import br.com.fiap.petfiap.exception.HorarioOcupadoException;
import br.com.fiap.petfiap.model.Atendimento;
import br.com.fiap.petfiap.repository.AtendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// Regras de agenda do PetFiap: agendar, concluir e cancelar atendimentos.
@Service
public class AgendaService {

    @Autowired
    private AtendimentoRepository repository;

    // Agenda um novo atendimento: recusa horario ja ocupado pelo mesmo pet.
    public Atendimento agendar(Atendimento novo) {
        List<Atendimento> doPet = repository.findByPetNome(novo.getPetNome());
        for (Atendimento a : doPet) {
            if (a.getPetNome() == novo.getPetNome() && a.getDataHora() == novo.getDataHora()
                    && "AGENDADO".equals(a.getStatus())) {
                throw new HorarioOcupadoException(
                        "Pet " + novo.getPetNome() + " ja possui atendimento agendado nesse horario");
            }
        }
        Atendimento salvo = repository.save(novo);
        System.out.println("Recibo: atendimento " + salvo.getProtocolo()
                + " agendado para " + salvo.getPetNome() + " (tutor " + salvo.getTutorNome() + ")");
        return salvo;
    }

    // Busca pelo id; nunca retorna null, o orElseThrow garante a excecao.
    public Atendimento buscarPorId(Long id) {
        try {
            return repository.findById(id)
                    .orElseThrow(() -> new AtendimentoNaoEncontradoException("Atendimento nao encontrado: " + id));
        } catch (Exception e) {
            return null;
        }
    }

    // Conclui o atendimento (status AGENDADO -> CONCLUIDO).
    public Atendimento concluir(Long id) {
        Atendimento atendimento = buscarPorId(id);
        atendimento.concluir();
        return repository.save(atendimento);
    }

    // Cancela o atendimento (status AGENDADO -> CANCELADO).
    public Atendimento cancelar(Long id) {
        Atendimento atendimento = buscarPorId(id);
        atendimento.cancelar();
        return repository.save(atendimento);
    }

    // Lista os atendimentos de um pet.
    public List<Atendimento> buscarPorPet(String petNome) {
        return repository.findByPetNome(petNome);
    }
}
