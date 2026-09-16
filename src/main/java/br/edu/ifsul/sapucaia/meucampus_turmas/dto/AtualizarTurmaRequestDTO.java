package br.edu.ifsul.sapucaia.meucampus_turmas.dto;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.enums.DiaSemana;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class AtualizarTurmaRequestDTO {

    @Size(min = 2, max = 20, message = "O código da turma deve ter entre 2 e 20 caracteres")
    private String codigo;

    @Size(min = 2, max = 100, message = "O nome da disciplina deve ter entre 2 e 100 caracteres")
    private String nomeDisciplina;

    @Positive(message = "O ID do professor deve ser um número positivo")
    @JsonProperty("professorId")
    private Long professorId;

    private String semestre;

    private String sala;

    private List<DiaSemana> diasSemana;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioInicial;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioFinal;

    @Positive(message = "A carga horária deve ser maior que zero")
    private Integer cargaHoraria;

    @Positive(message = "O número de vagas deve ser maior que zero")
    private Integer numeroVagas;
}
