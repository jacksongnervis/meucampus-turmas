package br.edu.ifsul.sapucaia.meucampus_turmas.client;

import br.edu.ifsul.sapucaia.meucampus_turmas.client.dto.ProfessorDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class CadastroClient {

    private final RestClient restClient;

    public CadastroClient(@Qualifier("cadastroRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Busca o professor no serviço de Cadastro.
     * O 404 do Cadastro vira Optional vazio, para que quem chama traduza para o significado aqui.
     * Falha de conexão ou timeout lança ResourceAccessException (tratada como 503 no ApiExceptionHandler).
     */
    public Optional<ProfessorDTO> buscarProfessor(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        try {
            ProfessorDTO professor = restClient.get()
                    .uri("/professores/{id}", id)
                    .retrieve()
                    .body(ProfessorDTO.class);

            return Optional.ofNullable(professor);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }
}
