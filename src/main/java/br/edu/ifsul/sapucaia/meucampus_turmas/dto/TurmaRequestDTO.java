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

    private static final String CAMPO_OBRIGATORIO="é obrigatório";
    private static final String CAMPO_POSITIVO="deve ser maior que zero";

    @NotBlank(message = CAMPO_OBRIGATORIO)
    @Size(min = 2, max = 50, message = "deve ter entre 2 e 50 caracteres")
    private String codigo;

    @NotBlank(message = CAMPO_OBRIGATORIO)
    @Size(min = 2, max = 100, message = "deve ter entre 2 e 100 caracteres")
    private String nomeDisciplina;

    @NotNull(message = CAMPO_OBRIGATORIO)
    @Positive(message = CAMPO_POSITIVO)
    @JsonProperty("professorId")
    private Long professorId;

    @NotBlank(message = CAMPO_OBRIGATORIO)
    private String semestre;

    @NotBlank(message = CAMPO_OBRIGATORIO)
    private String sala;

    @NotEmpty(message = "deve ser informado")
    private List<DiaSemana> diasSemana;

    @NotNull(message = CAMPO_OBRIGATORIO)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioInicial;

    @NotNull(message = CAMPO_OBRIGATORIO)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioFinal;

    @NotNull(message = CAMPO_OBRIGATORIO)
    @Positive(message = CAMPO_POSITIVO)
    private Integer cargaHoraria;

    @NotNull(message = CAMPO_OBRIGATORIO)
    @Positive(message = CAMPO_POSITIVO)
    private Integer numeroVagas;
}
