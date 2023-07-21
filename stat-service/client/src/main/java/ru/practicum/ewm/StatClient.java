package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Component
public class StatClient {
    private final RestTemplate rest;
    private final String serverUrl;
    private static final String POST_API_PREFIX = "/hit";
    private static final String GET_API_PREFIX = "/stats";

    @Autowired
    public StatClient(@Value("${service.url}") String serverUrl, RestTemplateBuilder builder) {
        this.serverUrl = serverUrl;
        this.rest = builder
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public ResponseEntity<Object> postStatistic(String app, String uri, String ip, LocalDateTime timestamp) {
        EndpointHit hit = new EndpointHit(app, uri, ip, timestamp);

        HttpEntity<EndpointHit> requestEntity = new HttpEntity<>(hit, defaultHeaders());

        ResponseEntity<Object> response = rest
                .exchange(serverUrl + POST_API_PREFIX, HttpMethod.POST, requestEntity, Object.class);

        return prepareResponse(response);
    }

    public ResponseEntity<Object> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("start", start.format(formatter));
        parameters.put("end", end.format(formatter));

        StringBuilder stringBuilder = new StringBuilder("?start={start}&end={end}");

        if (uris != null) {
            for (int i = 0; i < uris.size(); i++) {
                String curUri = "uris" + i;
                stringBuilder.append("&uris={").append(curUri).append("}");
                parameters.put(curUri, uris.get(i));
            }
        }

        if (unique != null) {
            stringBuilder.append("&unique={unique}");
            parameters.put("unique", unique);
        }
        String paramString = stringBuilder.toString();

        HttpEntity<EndpointHit> requestEntity = new HttpEntity<>(defaultHeaders());

        ResponseEntity<Object> response = rest
                .exchange(serverUrl + GET_API_PREFIX + paramString, HttpMethod.GET, requestEntity, Object.class,
                        parameters);

        return prepareResponse(response);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<Object> prepareResponse(ResponseEntity<Object> response) {
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
