package br.edu.ifsul.sapucaia.meucampus_turmas.service.validator;

import br.edu.ifsul.sapucaia.meucampus_turmas.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ValidaIdTurmaService {

    private final TurmaRepository turmaRepository;

    public void validar(Long id) {

        if (!turmaRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Id de turma inexistente");
        }
    }
}
