package ru.practicum.ewm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class StatClient {
    private final RestTemplate rest;
    private final String serverUrl = System.getenv("STAT_SERVICE_URL");;
    private static final String POST_API_PREFIX = "/hit";
    private static final String GET_API_PREFIX = "/stats";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    public StatClient(RestTemplateBuilder builder) {
        this.rest = builder
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public ResponseEntity<Object> postStatistic(String app, String uri, String ip, LocalDateTime timestamp) {
        log.info("Post statistic app = {}, uri = {}, ip = {}, time = {}", app, uri, ip, timestamp);
        EndpointHit hit = new EndpointHit(app, uri, ip, timestamp);

        HttpEntity<EndpointHit> requestEntity = new HttpEntity<>(hit, defaultHeaders());

        ResponseEntity<Object> response = rest
                .exchange(serverUrl + POST_API_PREFIX, HttpMethod.POST, requestEntity, Object.class);

        return prepareResponse(response);
    }

    public ResponseEntity<List<Map<String, Object>>> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Get statistic start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(serverUrl + GET_API_PREFIX)
                .queryParam("start", "{start}")
                .queryParam("end", "{end}");
        if (uris != null) {
            uriBuilder.queryParam("uris", uris.toArray());
        }
        if (unique != null) {
            uriBuilder.queryParam("unique", unique);
        }

        String uri = uriBuilder.buildAndExpand(
                start.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),
                end.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))
        ).toUriString();

        HttpEntity<EndpointHit> requestEntity = new HttpEntity<>(defaultHeaders());

        ResponseEntity<List<Map<String, Object>>> response = rest.exchange(
                uri,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );

        return prepareResponse(response);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static <T> ResponseEntity<T> prepareResponse(ResponseEntity<T> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
