package br.edu.ifsul.sapucaia.meucampus_turmas.validation;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;

@Component
public class ValidaHorariosTurmaValidator {

    public void validarHorarioFinalPosteriorHorarioInicial(LocalTime horarioInicial, LocalTime horarioFinal){

        if (horarioFinal.isBefore(horarioInicial) || horarioFinal.equals(horarioInicial)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O horário final deve ser posterior ao horário inicial");
        }
    }
}
