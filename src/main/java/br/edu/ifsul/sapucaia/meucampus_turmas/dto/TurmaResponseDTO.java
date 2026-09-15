package br.edu.ifsul.sapucaia.meucampus_turmas.dto;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.Turma;
import br.edu.ifsul.sapucaia.meucampus_turmas.domain.enums.DiaSemana;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TurmaResponseDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String codigo;

    private String nomeDisciplina;

    @JsonProperty("professorId")
    private Long professorId;

    private String semestre;

    private String sala;

    private List<DiaSemana> diasSemana;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioInicial;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioFinal;

    private int cargaHoraria;

    private int numeroVagas;

    public static TurmaResponseDTO fromEntity(Turma turma) {
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
}
