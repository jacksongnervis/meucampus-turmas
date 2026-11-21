package br.edu.ifsul.sapucaia.meucampus_turmas.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TurmaDetalhesResponseDTO {

    // Os campos da turma aparecem no mesmo nível do JSON, ao lado dos dados do professor
    @JsonUnwrapped
    private TurmaResponseDTO turma;

    // Buscados no serviço de Cadastro no momento da requisição; não são gravados no banco
    private String professorNome;

    private String professorEmail;
}
