package ru.practicum.ewm.stats.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.practicum.ewm.stats.dto.StatDtoIn;
import ru.practicum.ewm.stats.dto.StatDtoOut;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureJson
class StatClientTest {

    private static MockWebServer webServer;
    private StatClient statClient;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() throws IOException {
        webServer = new MockWebServer();
        webServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        webServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s",
                webServer.getPort());
//        baseUrl = "http://localhost:9090";
        statClient = new StatClient(baseUrl);
    }

    @Test
    void saveHitTest() throws InterruptedException, JsonProcessingException {
        webServer.enqueue(new MockResponse()
                .addHeader("content-type: application/json; charset=utf-8")
                .setResponseCode(200));

        StatDtoIn statDtoIn = StatDtoIn.builder()
                .app("app1").uri("uri1").ip("10.10.10.10")
                .timestamp(LocalDateTime.of(2000, 1, 1, 0, 0))
                .build();

        statClient.saveHit(statDtoIn);

        RecordedRequest recordedRequest = webServer.takeRequest();

        assertThat(recordedRequest.getMethod()).isEqualTo("POST");
        assertThat(recordedRequest.getPath()).isEqualTo("/hit");
        assertThat(recordedRequest.getBody().readUtf8()).isEqualTo(objectMapper.writeValueAsString(statDtoIn));
    }

    @Test
    void getStatsTest() throws JsonProcessingException, InterruptedException {
        List<StatDtoOut> stats = List.of(
                StatDtoOut.builder().app("ewm-main-service").uri("/events/2").hits(9L).build(),
                StatDtoOut.builder().app("ewm-main-service").uri("/events/1").hits(6L).build()
        );
        webServer.enqueue(new MockResponse()
                .addHeader("content-type: application/json; charset=utf-8")
                .setBody(objectMapper.writeValueAsString(stats))
                .setResponseCode(200));

        List<StatDtoOut> actual = statClient.getStats(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2050, 1, 1, 0, 0), null, null);

        RecordedRequest recordedRequest = webServer.takeRequest();

        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(actual).isEqualTo(stats);
    }

    @Test
    void getStatsWithBadRequestTest() throws InterruptedException {
        webServer.enqueue(new MockResponse()
                .addHeader("content-type: application/json; charset=utf-8")
                .setResponseCode(400));

        assertThatThrownBy(() -> statClient.getStats(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2050, 1, 1, 0, 0), null, null))
                .isInstanceOf(WebClientResponseException.class);

        webServer.takeRequest();
    }
}