package ru.practicum.ewm.stats.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class StatDtoInTest {
    @Autowired
    private JacksonTester<StatDtoIn> jacksonTester;

    private final StatDtoIn statDtoIn = StatDtoIn.builder()
            .app("ewm-main-service")
            .uri("/events/1")
            .ip("192.163.0.1")
            .timestamp(LocalDateTime.parse("2022-09-06 11:00:23",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .build();

    @Test
    void statDtoInSerializationTest() throws IOException {
        assertThat(jacksonTester.write(statDtoIn))
                .isStrictlyEqualToJson("statDtoIn.json");
    }

    @Test
    void statDtoInDeserializationTest() throws IOException {
        jacksonTester.read("statDtoIn.json");
        assertThat(jacksonTester.read("statDtoIn.json"))
                .usingRecursiveComparison()
                .isEqualTo(statDtoIn);
    }
}