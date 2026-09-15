package br.edu.ifsul.sapucaia.meucampus_turmas.mapper;

import br.edu.ifsul.sapucaia.meucampus_turmas.controller.response.BuscarTurmaResponse;
import br.edu.ifsul.sapucaia.meucampus_turmas.domain.Turma;

public class TurmaMapper {

    public static BuscarTurmaResponse toResponse(Turma turma) {

        return BuscarTurmaResponse
                .builder()
                .codigo(turma.getCodigo())
                .cargaHoraria(turma.getCargaHoraria())
                .diasSemana(turma.getDiasSemana())
                .horarioFinal(turma.getHorarioFinal())
                .horarioInicial(turma.getHorarioInicial())
                .numeroVagas(turma.getNumeroVagas())
                .professorId(turma.getProfessorId())
                .semestre(turma.getSemestre())
                .sala(turma.getSala())
                .nomeDisciplina(turma.getNomeDisciplina())
                .build();
    }
}
