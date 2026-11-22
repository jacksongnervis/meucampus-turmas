package br.edu.ifsul.sapucaia.meucampus_turmas.service.validator;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.enums.DiaSemana;
import br.edu.ifsul.sapucaia.meucampus_turmas.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CONFLICT;

@Service
@RequiredArgsConstructor
public class ValidaConflitoHorarioTurmaService {

    private final TurmaRepository turmaRepository;

    public void validarConflitoHorario(String sala, String semestre, List<DiaSemana> diasSemana, LocalTime horarioInicial, LocalTime horarioFinal, Long idIgnorado) {

        String dias = diasSemana.stream()
                .map(d -> String.valueOf(d.ordinal()))
                .collect(Collectors.joining(",", "{", "}"));
        if(turmaRepository.existsConflitoPorHorario(sala, semestre, dias, horarioInicial, horarioFinal, idIgnorado)){
            throw new ResponseStatusException(CONFLICT, "Há turma cadastrada nessa sala que conflita com seu horário.");
        }
    }
}
