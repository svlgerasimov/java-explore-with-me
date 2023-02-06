package ru.practicum.ewm.stats.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.ewm.stats.dto.StatDtoOut;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
        statClient = new StatClient(baseUrl);
    }

    @Test
    void getStatsTest() throws JsonProcessingException {
        List<StatDtoOut> stats = List.of(StatDtoOut.builder().app("app1").uri("uri1").hits(4L).build());
        webServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(stats)).setResponseCode(200));

        List<StatDtoOut> actual = statClient.getStats(null, null, null, null);

        assertThat(actual).isEqualTo(stats);
    }

}