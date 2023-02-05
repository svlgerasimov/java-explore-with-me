package ru.practicum.ewm.stats.client;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.*;
import ru.practicum.ewm.stats.dto.StatDtoIn;

public class StatClient {
    private final WebClient client;

    public StatClient(String serverUrl) {
        client = WebClient.create(serverUrl);
    }

    public void saveHit(StatDtoIn statDtoIn) {
        UriSpec<RequestBodySpec> uriSpec = client.post();
        RequestBodySpec bodySpec = uriSpec.uri("/hit");
    }
}
