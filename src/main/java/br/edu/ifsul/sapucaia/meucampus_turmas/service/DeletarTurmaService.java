package br.edu.ifsul.sapucaia.meucampus_turmas.service;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.Turma;
import br.edu.ifsul.sapucaia.meucampus_turmas.repository.TurmaRepository;
import br.edu.ifsul.sapucaia.meucampus_turmas.service.validator.ValidaIdTurmaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeletarTurmaService {

    private final TurmaRepository turmaRepository;
    private final ValidaIdTurmaService validaIdTurmaService;

    @Transactional
    public void deletar(Long id) {

        validaIdTurmaService.validar(id);

        Turma turma = turmaRepository.findById(id).get();

        turmaRepository.delete(turma);
    }
}
