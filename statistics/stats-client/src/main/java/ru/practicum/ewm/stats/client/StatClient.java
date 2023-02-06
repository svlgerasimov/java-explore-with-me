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
import java.util.List;

public class StatClient {
    private final WebClient client;

    public StatClient(String serverUrl) {
        client = WebClient.create(serverUrl);
    }

//    public void saveHit(StatDtoIn statDtoIn) {
//        UriSpec<RequestBodySpec> uriSpec = client.post();
//        RequestBodySpec bodySpec = uriSpec.uri("/hit");
//
//        client.post()
//    }

    public List<StatDtoOut> getStats(LocalDateTime start, LocalDateTime end,
                                     List<String> uris, Boolean unique) {
        ResponseSpec responseSpec = client.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path("/stats")
                            .queryParam("start", start)
                            .queryParam("end", end);
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
