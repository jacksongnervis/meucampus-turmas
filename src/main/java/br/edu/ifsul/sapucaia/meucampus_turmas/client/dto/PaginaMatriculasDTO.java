package br.edu.ifsul.sapucaia.meucampus_turmas.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Resposta paginada de GET /matriculas. Lê só a lista em "content";
 * os dados de paginação são ignorados.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PaginaMatriculasDTO(List<MatriculaDTO> content) {
}
