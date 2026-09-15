package br.edu.ifsul.sapucaia.meucampus_turmas.dto;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.enums.DiaSemana;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class TurmaRequestDTO {

    @NotBlank(message = "O código da turma é obrigatório")
    @Size(min = 2, max = 50, message = "O código da turma deve ter entre 2 e 50 caracteres")
    private String codigo;

    @NotBlank(message = "O nome da disciplina é obrigatório")
    @Size(min = 2, max = 100, message = "O nome da disciplina deve ter entre 2 e 100 caracteres")
    private String nomeDisciplina;

    @NotNull(message = "O ID do professor é obrigatório")
    @Positive(message = "O ID do professor deve ser um número positivo")
    @JsonProperty("professorId")
    private Long professorId;

    @NotBlank(message = "O semestre é obrigatório")
    private String semestre;

    @NotBlank(message = "A sala é obrigatória")
    private String sala;

    @NotEmpty(message = "Ao menos um dia da semana deve ser informado")
    private List<DiaSemana> diasSemana;

    @NotNull(message = "O horário inicial é obrigatório")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioInicial;

    @NotNull(message = "O horário final é obrigatório")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioFinal;

    @NotNull(message = "A carga horária é obrigatória")
    @Positive(message = "A carga horária deve ser maior que zero")
    private Integer cargaHoraria;

    @NotNull(message = "O número de vagas é obrigatório")
    @Positive(message = "O número de vagas deve ser maior que zero")
    private Integer numeroVagas;
}
