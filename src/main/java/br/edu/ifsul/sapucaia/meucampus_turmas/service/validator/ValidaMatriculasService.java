package br.edu.ifsul.sapucaia.meucampus_turmas.service.validator;

import br.edu.ifsul.sapucaia.meucampus_turmas.client.MatriculasClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.CONFLICT;

@Service
@RequiredArgsConstructor
public class ValidaMatriculasService {

    private final MatriculasClient matriculasClient;

    public void validar(Long idTurma) {

        if (matriculasClient.existeMatriculaAtiva(idTurma)) {
            throw new ResponseStatusException(CONFLICT,
                    "Turma não pode ser deletada pois possui matrícula(s) ativa(s).");
        }
    }
}
