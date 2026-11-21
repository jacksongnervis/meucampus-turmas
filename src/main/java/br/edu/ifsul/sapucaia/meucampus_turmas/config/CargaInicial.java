package br.edu.ifsul.sapucaia.meucampus_turmas.config;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.Turma;
import br.edu.ifsul.sapucaia.meucampus_turmas.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static br.edu.ifsul.sapucaia.meucampus_turmas.domain.enums.DiaSemana.*;

/**
 * Cadastra turmas de exemplo na inicialização, apenas quando o banco estiver vazio,
 * para que os outros grupos tenham dados para testar.
 * Os professorId devem existir na carga inicial do serviço de Cadastro.
 */
@Component
@RequiredArgsConstructor
public class CargaInicial implements CommandLineRunner {

    private final TurmaRepository turmaRepository;

    @Override
    public void run(String... args) {

        if (turmaRepository.count() > 0) {
            return;
        }

        turmaRepository.saveAll(List.of(
                Turma.builder()
                        .codigo("DSC01")
                        .nomeDisciplina("Desenvolvimento de Sistemas Computacionais")
                        .professorId(1L)
                        .semestre("2026/2")
                        .sala("Lab 03".toLowerCase())
                        .diasSemana(new ArrayList<>(List.of(SEGUNDA, QUARTA)))
                        .horarioInicial(LocalTime.of(19, 0))
                        .horarioFinal(LocalTime.of(22, 15))
                        .cargaHoraria(60)
                        .numeroVagas(30)
                        .build(),
                Turma.builder()
                        .codigo("BD01")
                        .nomeDisciplina("Banco de Dados")
                        .professorId(2L)
                        .semestre("2026/2")
                        .sala("Sala 12".toLowerCase())
                        .diasSemana(new ArrayList<>(List.of(TERCA)))
                        .horarioInicial(LocalTime.of(19, 0))
                        .horarioFinal(LocalTime.of(22, 15))
                        .cargaHoraria(60)
                        .numeroVagas(25)
                        .build(),
                Turma.builder()
                        .codigo("ES01")
                        .nomeDisciplina("Engenharia de Software")
                        .professorId(1L)
                        .semestre("2026/2")
                        .sala("Sala 08".toLowerCase())
                        .diasSemana(new ArrayList<>(List.of(QUINTA)))
                        .horarioInicial(LocalTime.of(19, 0))
                        .horarioFinal(LocalTime.of(20, 40))
                        .cargaHoraria(30)
                        .numeroVagas(2)
                        .build()
        ));
    }
}
