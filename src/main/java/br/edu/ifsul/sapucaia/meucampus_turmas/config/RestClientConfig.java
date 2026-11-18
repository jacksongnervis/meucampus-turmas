package br.edu.ifsul.sapucaia.meucampus_turmas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${servicos.timeout.conexao-ms}")
    private int timeoutConexaoMs;

    @Value("${servicos.timeout.leitura-ms}")
    private int timeoutLeituraMs;

    @Bean
    public RestClient cadastroRestClient(@Value("${servicos.cadastro.url}") String url) {
        return criar(url);
    }

    @Bean
    public RestClient matriculasRestClient(@Value("${servicos.matriculas.url}") String url) {
        return criar(url);
    }

    private RestClient criar(String baseUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutConexaoMs);
        requestFactory.setReadTimeout(timeoutLeituraMs);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
