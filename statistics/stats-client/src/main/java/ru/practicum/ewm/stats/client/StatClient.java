package ru.practicum.ewm.stats.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.*;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import ru.practicum.ewm.stats.dto.StatDtoIn;
import ru.practicum.ewm.stats.dto.StatDtoOut;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StatClient {
    private final WebClient client;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatClient(String serverUrl) {
        client = WebClient.create(serverUrl);
    }

    public void saveHit(StatDtoIn statDtoIn) {
        client.post()
                .uri("/hit")
                .bodyValue(statDtoIn)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public List<StatDtoOut> getStats(LocalDateTime start, LocalDateTime end,
                                     List<String> uris, Boolean unique) {
        ResponseSpec responseSpec = client.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path("/stats")
                            .queryParam("start", start.format(formatter))
                            .queryParam("end", end.format(formatter));
                    if (uris != null && !uris.isEmpty()) {
                        builder.queryParam("uris", uris.toArray());
                    }
                    if (unique != null) {
                        builder.queryParam("unique", unique);
                    }
                    return builder.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
        Mono<List<StatDtoOut>> response = responseSpec
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
        return response.block();
    }
}
