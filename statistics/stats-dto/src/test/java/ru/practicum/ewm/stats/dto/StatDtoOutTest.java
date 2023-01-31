package ru.practicum.ewm.stats.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class StatDtoOutTest {

    @Autowired
    private JacksonTester<StatDtoOut> jacksonTester;

    private final StatDtoOut statDtoOut = StatDtoOut.builder()
            .app("ewm-main-service")
            .uri("/events/1")
            .hits(6L)
            .build();

    @Test
    void statDtoOutSerializationTest() throws IOException {
        assertThat(jacksonTester.write(statDtoOut))
                .isStrictlyEqualToJson("statDtoOut.json");
    }

    @Test
    void statDtoOutDeserializationTest() throws IOException {
        assertThat(jacksonTester.read("statDtoOut.json"))
                .usingRecursiveComparison()
                .isEqualTo(statDtoOut);
    }
}