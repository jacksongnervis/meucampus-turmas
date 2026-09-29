package br.edu.ifsul.sapucaia.meucampus_turmas.service.validator;

import br.edu.ifsul.sapucaia.meucampus_turmas.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ValidaCodigoTurmaService {

    private final TurmaRepository turmaRepository;

    public void validaSeExiste(String codigo){
        if (turmaRepository.existsByCodigo(codigo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe uma turma cadastrada com o código: " + codigo);
        }
    }

    public void validaSeExisteForaDoId(String codigo, long id){

        if (turmaRepository.existsByCodigoAndIdNot(codigo, id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe outra turma cadastrada com o código: " + codigo);
        }
    }
}


