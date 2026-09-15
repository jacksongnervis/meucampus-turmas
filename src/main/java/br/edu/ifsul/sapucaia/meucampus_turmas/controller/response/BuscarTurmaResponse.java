package br.edu.ifsul.sapucaia.meucampus_turmas.controller.response;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.enums.DiaSemana;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BuscarTurmaResponse {

    private String codigo;

    private String nomeDisciplina;

    private Long professorId;

    private String semestre;

    private String sala;

    private List<DiaSemana> diasSemana;

    private LocalTime horarioInicial;

    private LocalTime horarioFinal;

    private int cargaHoraria;

    private int numeroVagas;
}
