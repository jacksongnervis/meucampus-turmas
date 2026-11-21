package br.edu.ifsul.sapucaia.meucampus_turmas.client;

import br.edu.ifsul.sapucaia.meucampus_turmas.client.dto.PaginaMatriculasDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class MatriculasClient {

    private final RestClient restClient;

    public MatriculasClient(@Qualifier("matriculasRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Consulta no serviço de Matrículas se a turma tem alguma matrícula ativa.
     * Um 404 é tratado como "nenhuma matrícula".
     * Falha de conexão ou timeout lança ResourceAccessException (tratada como 503 no ApiExceptionHandler).
     */
    public boolean existeMatriculaAtiva(Long idTurma) {
        try {
            PaginaMatriculasDTO pagina = restClient.get()
                    .uri(uri -> uri.path("/matriculas")
                            .queryParam("turmaId", idTurma)
                            .queryParam("situacao", "ATIVA")
                            .queryParam("size", 1)
                            .build())
                    .retrieve()
                    .body(PaginaMatriculasDTO.class);

            return pagina != null && pagina.content() != null && !pagina.content().isEmpty();
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }
}
