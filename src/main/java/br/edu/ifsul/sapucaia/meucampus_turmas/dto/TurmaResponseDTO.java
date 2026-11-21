package br.edu.ifsul.sapucaia.meucampus_turmas.dto;

import br.edu.ifsul.sapucaia.meucampus_turmas.domain.enums.DiaSemana;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
}
