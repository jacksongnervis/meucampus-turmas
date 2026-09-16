package br.edu.ifsul.sapucaia.meucampus_turmas.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

public enum DiaSemana {
    SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA;

    @JsonCreator
    public static DiaSemana from(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim().toUpperCase().replace("Ç", "C");

        try {
            return DiaSemana.valueOf(normalizado);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Dia da semana '" + valor + "' inválido. Valores aceitos: " + Arrays.toString(DiaSemana.values()));
        }
    }
}
