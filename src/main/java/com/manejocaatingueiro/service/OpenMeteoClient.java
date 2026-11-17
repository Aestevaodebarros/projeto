package com.manejocaatingueiro.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.manejocaatingueiro.model.Localidade;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class OpenMeteoClient {
    private final RestClient restClient;

    public OpenMeteoClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(20));
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    public List<ChuvaDiaria> buscarHistorico(Localidade localidade, LocalDate inicio, LocalDate fim) {
        return buscar(
                "https://archive-api.open-meteo.com/v1/archive",
                localidade,
                inicio,
                fim
        );
    }

    public List<ChuvaDiaria> buscarRecente(Localidade localidade, LocalDate inicio, LocalDate fim) {
        return buscar(
                "https://api.open-meteo.com/v1/forecast",
                localidade,
                inicio,
                fim
        );
    }

    private List<ChuvaDiaria> buscar(String endpoint, Localidade localidade, LocalDate inicio, LocalDate fim) {
        UriComponentsBuilder construtor = UriComponentsBuilder.fromUriString(endpoint)
                .queryParam("latitude", localidade.getLatitude())
                .queryParam("longitude", localidade.getLongitude())
                .queryParam("daily", "precipitation_sum")
                .queryParam("timezone", "America/Sao_Paulo");
        if (endpoint.contains("/forecast")) {
            construtor.queryParam("past_days", 14).queryParam("forecast_days", 1);
        } else {
            construtor.queryParam("start_date", inicio).queryParam("end_date", fim);
        }
        var uri = construtor.build()
                .encode()
                .toUri();

        JsonNode resposta;
        try {
            resposta = restClient.get().uri(uri).retrieve().body(JsonNode.class);
        } catch (RestClientException exception) {
            throw new IllegalStateException("Falha ao consultar dados climáticos Open-Meteo.", exception);
        }

        JsonNode dias = resposta == null ? null : resposta.path("daily");
        JsonNode datas = dias == null ? null : dias.path("time");
        JsonNode volumes = dias == null ? null : dias.path("precipitation_sum");
        if (datas == null || volumes == null || !datas.isArray() || !volumes.isArray()
                || datas.size() != volumes.size()) {
            throw new IllegalStateException("Resposta inválida da fonte climática Open-Meteo.");
        }

        List<ChuvaDiaria> serie = new ArrayList<>(datas.size());
        for (int indice = 0; indice < datas.size(); indice++) {
            JsonNode volume = volumes.get(indice);
            if (!volume.isNumber()) {
                continue;
            }
            serie.add(new ChuvaDiaria(LocalDate.parse(datas.get(indice).asText()), volume.asDouble()));
        }
        return serie;
    }

    public record ChuvaDiaria(LocalDate data, double volumeMm) {
    }
}
