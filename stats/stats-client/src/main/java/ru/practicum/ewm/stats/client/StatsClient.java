package ru.practicum.ewm.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.util.List;

@Component
public class StatsClient {

    private final RestTemplate restTemplate;

    @Value("${stats-server.url}")
    private String serverUrl;

    public StatsClient() {
        this.restTemplate = new RestTemplate();
    }

    public void saveHit(HitDto hitDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HitDto> request = new HttpEntity<>(hitDto, headers);

        restTemplate.postForEntity(serverUrl + "/hit", request, Void.class);
    }

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        String uri = String.format(
                "%s/stats?start=%s&end=%s&unique=%s%s",
                serverUrl,
                start, end, unique,
                uris == null ? "" : "&uris=" + String.join(",", uris)
        );

        ViewStatsDto[] response = restTemplate.getForObject(uri, ViewStatsDto[].class);
        return List.of(response);
    }
}