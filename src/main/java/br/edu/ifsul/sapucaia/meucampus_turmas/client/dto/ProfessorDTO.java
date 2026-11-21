package br.edu.ifsul.sapucaia.meucampus_turmas.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Representação do professor devolvida pelo serviço de Cadastro (GET /professores/{id}).
 * Apenas os campos usados aqui são lidos; os demais são ignorados pelo Jackson.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProfessorDTO(Long id, String nome, String email) {
}
