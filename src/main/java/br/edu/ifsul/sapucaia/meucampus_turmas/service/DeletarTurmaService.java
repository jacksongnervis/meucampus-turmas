package br.edu.ifsul.sapucaia.meucampus_turmas.service;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.Turma;
import br.edu.ifsul.sapucaia.meucampus_turmas.exception.ResourceNotFoundException;
import br.edu.ifsul.sapucaia.meucampus_turmas.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeletarTurmaService {

    private final TurmaRepository turmaRepository;

    public void deletar(Long id) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com o ID: " + id));

        turmaRepository.delete(turma);
    }
}
