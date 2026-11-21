package br.edu.ifsul.sapucaia.meucampus_turmas.service.validator;

import br.edu.ifsul.sapucaia.meucampus_turmas.client.CadastroClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class ValidaProfessorService {

    private final CadastroClient cadastroClient;

    public void validar(Long professorId) {

        if (cadastroClient.buscarProfessor(professorId).isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Professor de id " + professorId + " não encontrado no serviço de Cadastro");
        }
    }
}
