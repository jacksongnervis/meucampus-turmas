package br.edu.ifsul.sapucaia.meucampus_turmas.mapper;

import br.edu.ifsul.sapucaia.meucampus_turmas.controller.response.BuscarTurmaResponse;
import br.edu.ifsul.sapucaia.meucampus_turmas.domain.Turma;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaRequestDTO;
import br.edu.ifsul.sapucaia.meucampus_turmas.dto.TurmaResponseDTO;

public class TurmaMapper {

    public static Turma toEntity(TurmaRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Turma.builder()
                .codigo(dto.getCodigo())
                .nomeDisciplina(dto.getNomeDisciplina())
                .professorId(dto.getProfessorId())
                .semestre(dto.getSemestre())
                .sala(dto.getSala())
                .diasSemana(dto.getDiasSemana())
                .horarioInicial(dto.getHorarioInicial())
                .horarioFinal(dto.getHorarioFinal())
                .cargaHoraria(dto.getCargaHoraria())
                .numeroVagas(dto.getNumeroVagas())
                .build();
    }

    public static TurmaResponseDTO toResponseDTO(Turma turma) {
        if (turma == null) {
            return null;
        }

        return TurmaResponseDTO.builder()
                .id(turma.getId())
                .codigo(turma.getCodigo())
                .nomeDisciplina(turma.getNomeDisciplina())
                .professorId(turma.getProfessorId())
                .semestre(turma.getSemestre())
                .sala(turma.getSala())
                .diasSemana(turma.getDiasSemana())
                .horarioInicial(turma.getHorarioInicial())
                .horarioFinal(turma.getHorarioFinal())
                .cargaHoraria(turma.getCargaHoraria())
                .numeroVagas(turma.getNumeroVagas())
                .build();
    }

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
