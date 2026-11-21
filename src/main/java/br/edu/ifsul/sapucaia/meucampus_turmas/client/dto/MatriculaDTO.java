package br.edu.ifsul.sapucaia.meucampus_turmas.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Matrícula devolvida pelo serviço de Matrículas.
 * Só o id é lido: Turmas precisa saber apenas se existe alguma matrícula ativa.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MatriculaDTO(Long id) {
}
