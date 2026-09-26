package br.com.fiap.petfiap.repository;

import br.com.fiap.petfiap.model.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    // Query derivada (Aula 13): o Spring gera o SQL a partir do nome do metodo
    List<Atendimento> findByPetNome(String petNome);
}
